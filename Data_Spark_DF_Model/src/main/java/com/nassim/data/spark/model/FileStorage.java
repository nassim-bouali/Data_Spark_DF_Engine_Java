package com.nassim.data.spark.model;

public abstract class FileStorage extends Storage {
    public abstract String getStorageAccount();
    public abstract String getContainer();
    public abstract String getPath();
    public abstract String getSasToken();
    public String getAbsolutePath(){
        return getStorageAccount() != null ? "abfss://" +getContainer()+ "@" +getStorageAccount()+ ".dfs.core.windows.net/"+getPath() : getPath();
    };
}
