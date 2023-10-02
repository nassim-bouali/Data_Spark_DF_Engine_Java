package com.nassim.data.spark.service.writer;

import com.nassim.data.spark.model.CsvStorage;
import com.nassim.data.spark.model.Storage;
import com.nassim.data.spark.model.StorageType;
import com.nassim.data.spark.service.IncompatibleTypeException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;

public class CsvDataWriter implements DataWriter{

    @Override
    public void write(Dataset<Row> dataset, Storage output) {
        if (isCompatible(output)){
            CsvStorage csvOutput = (CsvStorage) output;
            System.out.println(csvOutput.getAbsolutePath());
            dataset.write()
                    .options(csvOutput.getOptions())
                    .mode(SaveMode.Overwrite)
                    .csv(csvOutput.getAbsolutePath());
        }
        else
            throw new IncompatibleTypeException();
    }

    @Override
    public boolean isCompatible(Storage output) {
        return output.getType().equals(StorageType.csv);
    }
}
