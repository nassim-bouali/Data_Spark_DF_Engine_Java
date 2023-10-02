package com.nassim.data.spark.service;

import com.nassim.data.spark.model.Storage;
import com.nassim.data.spark.service.reader.DataReader;
import com.nassim.data.spark.service.transformer.DataTransformer;
import com.nassim.data.spark.service.writer.DataWriter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@AllArgsConstructor
public class DefaultDataProcessor implements DataProcessor{

    private List<DataReader> dataReaderList;
    private List<DataTransformer> dataTransformerList;
    private List<DataWriter> dataWriterList;

    @Override
    public void process(Storage input, Storage output) {
        System.out.println("Starting Processing Data from input: "+ input +" to output: "+ output);
        Optional<DataReader> dataReader = dataReaderList.stream().filter(reader -> reader.isCompatible(input)).findFirst();
        Optional<DataWriter> dataWriter = dataWriterList.stream().filter(writer -> writer.isCompatible(output)).findFirst();
        if (dataReader.isPresent() && dataWriter.isPresent()){
            System.out.println("Reading data from: "+ input);
            AtomicReference<Dataset<Row>> inputDataSet = new AtomicReference<>(dataReader.get().read(input));
            System.out.println("Transforming data.. ");
            dataTransformerList.stream().forEach(dataTransformer -> inputDataSet.set(dataTransformer.transform(inputDataSet.get())));
            System.out.println("Writing data to: "+ output);
            dataWriter.get().write(inputDataSet.get(), output);
            System.out.println("Data written correctly");
        }
        else
            System.out.println("Either input or output types are still not supported.");
    }
}
