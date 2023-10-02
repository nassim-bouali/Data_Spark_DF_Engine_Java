package com.nassim.data.spark.service.transformer;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public interface DataTransformer {
    public Dataset<Row> transform(Dataset<Row> inputData);
}
