package com.nassim.data.spark.service.reader;

import com.nassim.data.spark.model.CsvStorage;
import com.nassim.data.spark.model.Storage;
import com.nassim.data.spark.model.StorageType;
import com.nassim.data.spark.service.IncompatibleTypeException;
import lombok.AllArgsConstructor;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

@AllArgsConstructor
public class CsvDataReader implements DataReader{

    private SparkSession sparkSession;

    @Override
    public Dataset<Row> read(Storage input) {
        if (isCompatible(input)) {
            CsvStorage csvInput = (CsvStorage) input;
            Dataset<Row> inputDataset = sparkSession.read()
                    .options(csvInput.getOptions())
                    .csv(csvInput.getAbsolutePath());
            return csvInput.isCache() ? inputDataset.cache() : inputDataset;
        }
        throw new IncompatibleTypeException();
    }

    public boolean isCompatible(Storage input){
        return input.getType().equals(StorageType.csv);
    }
}
