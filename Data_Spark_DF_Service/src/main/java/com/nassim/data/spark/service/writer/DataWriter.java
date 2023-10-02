package com.nassim.data.spark.service.writer;

import com.nassim.data.spark.model.Storage;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public interface DataWriter {
    public void write(Dataset<Row> dataset, Storage output);
    public boolean isCompatible(Storage output);
}
