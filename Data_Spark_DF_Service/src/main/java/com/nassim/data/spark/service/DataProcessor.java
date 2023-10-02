package com.nassim.data.spark.service;

import com.nassim.data.spark.model.Storage;

public interface DataProcessor {
    public void process(Storage input, Storage output);
}
