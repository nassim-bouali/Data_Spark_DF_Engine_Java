package com.nassim.data.spark.model;

import lombok.Getter;

@Getter
public abstract class Storage {
    private StorageType type;
    public abstract boolean isCache();
}
