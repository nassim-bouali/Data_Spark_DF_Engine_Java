package com.nassim.data.spark.service.writer;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.nassim.data.spark.model.JdbcStorage;
import com.nassim.data.spark.model.Storage;
import com.nassim.data.spark.model.StorageType;
import com.nassim.data.spark.service.IncompatibleTypeException;
import com.nassim.data.spark.service.configuration.StorageUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Properties;

public class JdbcDataWriter implements DataWriter{

    @Override
    public void write(Dataset<Row> dataset, Storage output) {
        if (isCompatible(output)){
            JdbcStorage jdbcOutput = (JdbcStorage) output;
            dataset.write()
                    .mode(SaveMode.Overwrite)
                    .jdbc(jdbcOutput.getUri(),
                            jdbcOutput.getTable(),
                            getProperties(jdbcOutput.getOptions()));
        }
        else
            throw new IncompatibleTypeException();
    }

    public Properties getProperties(Map<String, String> options){
        Properties connectionProps = new Properties();
        connectionProps.put("loginTimeout", "300");
        if (options == null || !options.containsKey("password")){
            connectionProps.put("user", "");
            connectionProps.put("password", "");
            connectionProps.put("accessToken", StorageUtils.getJdbcAccessToken());
        }
        else
            connectionProps.putAll(options);

        return connectionProps;
    }

    @Override
    public boolean isCompatible(Storage output) {
        return output.getType().equals(StorageType.sql);
    }
}
