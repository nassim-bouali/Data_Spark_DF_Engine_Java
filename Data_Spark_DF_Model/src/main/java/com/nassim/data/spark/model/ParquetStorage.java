package com.nassim.data.spark.model;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Builder
@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class ParquetStorage extends FileStorage {
    private StorageType type = StorageType.parquet;
    private String storageAccount;
    private String container;
    private String sasToken;
    private String path;
    private boolean cache;
    private Map<String, String> options;
}
