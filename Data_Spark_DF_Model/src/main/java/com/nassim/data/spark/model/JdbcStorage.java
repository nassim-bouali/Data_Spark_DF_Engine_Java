package com.nassim.data.spark.model;

import lombok.Builder;
import lombok.Value;

import java.util.Map;

@Builder
@Value
public class JdbcStorage extends Storage {
    private StorageType type = StorageType.sql;
    private String database;
    private String schema;
    private String table;
    private String request;
    private String uri;
    private boolean cache;
    private Map<String, String> options;
}
