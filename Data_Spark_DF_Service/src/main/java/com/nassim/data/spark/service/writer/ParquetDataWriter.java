package com.nassim.data.spark.service.writer;

import com.nassim.data.spark.model.ParquetStorage;
import com.nassim.data.spark.model.Storage;
import com.nassim.data.spark.model.StorageType;
import com.nassim.data.spark.service.IncompatibleTypeException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;

public class ParquetDataWriter implements DataWriter{

    @Override
    public void write(Dataset<Row> dataset, Storage output) {
        if (isCompatible(output)){
            ParquetStorage parquetOutput = (ParquetStorage) output;
            dataset.write()
                    .options(parquetOutput.getOptions())
                    .mode(SaveMode.Overwrite)
                    .parquet(parquetOutput.getAbsolutePath());
        }
        else
            throw new IncompatibleTypeException();
    }

    @Override
    public boolean isCompatible(Storage output) {
        return output.getType().equals(StorageType.parquet);
    }
}
