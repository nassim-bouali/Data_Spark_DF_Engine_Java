package com.nassim.data.spark.service.configuration;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.sas.BlobContainerSasPermission;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.nassim.data.spark.model.CsvStorage;
import com.nassim.data.spark.model.FileStorage;
import com.nassim.data.spark.model.ParquetStorage;
import com.nassim.data.spark.model.Storage;
import org.apache.hadoop.conf.Configuration;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;

public class StorageUtils {
    public static void updateHadoopConfig(List<Storage> storageList, Configuration config){
        Configuration storageConfig = new Configuration(false);
        storageList.stream()
                .filter(storage -> storage instanceof CsvStorage || storage instanceof ParquetStorage)
                .map(storage -> (FileStorage) storage)
                .forEach(fileStorage -> {
                    if (fileStorage.getStorageAccount() != null){
                        storageConfig.set(String.format("fs.azure.sas.token.provider.type.%s.dfs.core.windows.net", fileStorage.getStorageAccount()), "com.nassim.data.spark.service.configuration.SasTokenProvider");
                        storageConfig.set(String.format("fs.azure.account.auth.type.%s.dfs.core.windows.net", fileStorage.getStorageAccount()), "SAS");
                        storageConfig.set(String.format("%s.%s", fileStorage.getStorageAccount(), fileStorage.getContainer()), getSasToken(fileStorage));
                    }
                });
        merge(storageConfig, config);
    }

    public static void merge(Configuration source, Configuration target){
        source.forEach(config -> target.set(config.getKey(), config.getValue()));
    }

    public static String getSasToken(FileStorage fileStorage){
        return fileStorage.getSasToken() != null ? fileStorage.getSasToken() : getSasTokenWithUserDelegationKey(fileStorage);
    }

    public static String getSasTokenWithUserDelegationKey(FileStorage fileStorage){
        // Use the DefaultAzureCredentialBuilder to authenticate without an account key
        // This will automatically use available credentials like Managed Identity or Azure CLI
        DefaultAzureCredentialBuilder credentialBuilder = new DefaultAzureCredentialBuilder();

        // Build the BlobServiceClient with authentication
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .endpoint("https://" + fileStorage.getStorageAccount() + ".blob.core.windows.net")
                .credential(credentialBuilder.build())
                .buildClient();

        // Get a reference to the container
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(fileStorage.getContainer());

        // Define the permissions and expiration for the SAS token
        BlobContainerSasPermission containerSasPermission = new BlobContainerSasPermission()
                .setReadPermission(true)
                .setListPermission(true)
                .setWritePermission(true);

        OffsetDateTime expirationTime = OffsetDateTime.now().plusHours(1);

        // Generate the SAS token values
        BlobServiceSasSignatureValues sasValues = new BlobServiceSasSignatureValues(expirationTime, containerSasPermission)
                .setStartTime(OffsetDateTime.now());

        // Generate the SAS token for the container
        String sasToken = containerClient.generateSas(sasValues);

        return sasToken;
    }

    public static String getJdbcAccessToken(){
        DefaultAzureCredential credential = new DefaultAzureCredentialBuilder().build();

        // Specify the token request context
        TokenRequestContext requestContext = new TokenRequestContext().addScopes("https://database.azure.net/.default");

        // Obtain the access token asynchronously
        Mono<AccessToken> tokenMono = credential.getToken(requestContext);

        // Block and get the access token
        AccessToken token = tokenMono.block();
        String accessToken = token.getToken();
        System.out.println("Access Token: " + accessToken);

        return accessToken;
    }
}
