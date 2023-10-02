package com.nassim.data.spark.service.transformer;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class DefaultDataTransformer implements DataTransformer{
    @Override
    public Dataset<Row> transform(Dataset<Row> inputData) {
        return inputData.select("*");
    }
}
