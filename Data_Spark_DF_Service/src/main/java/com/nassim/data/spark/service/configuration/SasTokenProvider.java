package com.nassim.data.spark.service.configuration;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.azurebfs.extensions.SASTokenProvider;

import java.io.IOException;

public class SasTokenProvider implements SASTokenProvider {
    private Configuration configuration;

    @Override
    public void initialize(Configuration configuration, String s) throws IOException {
        // Initialize the SAS token provider with the Hadoop Configuration.
        this.configuration = configuration;
        System.out.println(configuration);
    }

    @Override
    public String getSASToken(String storageAccount, String container, String s2, String s3) {
        String sasToken = configuration.get(String.format("%s.%s", storageAccount, container));
        return sasToken;
    }
}
