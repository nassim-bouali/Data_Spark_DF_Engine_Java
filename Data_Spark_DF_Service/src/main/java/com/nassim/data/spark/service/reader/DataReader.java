package com.nassim.data.spark.service.reader;

import com.nassim.data.spark.model.Storage;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public interface DataReader {
    public Dataset<Row> read(Storage input);
    public boolean isCompatible(Storage input);
}
