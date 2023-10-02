package com.nassim.data.spark.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nassim.data.spark.model.CsvStorage;
import com.nassim.data.spark.model.JdbcStorage;
import com.nassim.data.spark.model.ParquetStorage;
import com.nassim.data.spark.model.Storage;
import com.nassim.data.spark.service.DefaultDataProcessor;
import com.nassim.data.spark.service.configuration.StorageUtils;
import com.nassim.data.spark.service.reader.CsvDataReader;
import com.nassim.data.spark.service.reader.ParquetDataReader;
import com.nassim.data.spark.service.transformer.DefaultDataTransformer;
import com.nassim.data.spark.service.writer.CsvDataWriter;
import com.nassim.data.spark.service.writer.JdbcDataWriter;
import com.nassim.data.spark.service.writer.ParquetDataWriter;
import org.apache.spark.sql.SparkSession;
import picocli.CommandLine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

@CommandLine.Command
public class Application implements Callable<Integer> {

    @CommandLine.Option(names = "--input-type", description = "input type", required = true)
    private String inputType = "";
    @CommandLine.Option(names = "--input-storage-account", description = "input storage account", required = false)
    private String inputStorageAccount;
    @CommandLine.Option(names = "--input-container", description = "input container", required = false)
    private String inputContainer;
    @CommandLine.Option(names = "--input-path", description = "input path", required = false)
    private String inputPath;
    @CommandLine.Option(names = "--input-sas-token", description = "input sas token", required = false)
    private String inputSasToken;
    @CommandLine.Option(names = "--input-cache", description = "input cache", required = false)
    private boolean inputCache = false;
    @CommandLine.Option(names = "--input-table", description = "input table name", required = false)
    private String inputTable;
    @CommandLine.Option(names = "--input-uri", description = "input database uri", required = false)
    private String inputUri;
    @CommandLine.Option(names = "--input-options", description = "input options", required = false)
    private String inputOptions;

    @CommandLine.Option(names = "--output-type", description = "output type", required = true)
    private String outputType;
    @CommandLine.Option(names = "--output-storage-account", description = "output storage account", required = false)
    private String outputStorageAccount;
    @CommandLine.Option(names = "--output-container", description = "output container", required = false)
    private String outputContainer;
    @CommandLine.Option(names = "--output-path", description = "output path", required = false)
    private String outputPath;
    @CommandLine.Option(names = "--output-sas-token", description = "output sas token", required = false)
    private String outputSasToken;
    @CommandLine.Option(names = "--output-cache", description = "output cache", required = false)
    private boolean outputCache = false;
    @CommandLine.Option(names = "--output-table", description = "output table name", required = false)
    private String outputTable;
    @CommandLine.Option(names = "--output-uri", description = "output database uri", required = false)
    private String outputUri;
    @CommandLine.Option(names = "--output-options", description = "output options", required = false)
    private String outputOptions;



    public static void main(String[] args) {
        int exitCode = new CommandLine(new Application()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        SparkSession sparkSession = SparkSession.builder().master("local").appName("Spark DataFrame Engine Example").getOrCreate();
        ObjectMapper objectMapper = new ObjectMapper();

        DefaultDataProcessor processor = new DefaultDataProcessor(new ArrayList<>(Arrays.asList(new CsvDataReader(sparkSession), new ParquetDataReader(sparkSession))),
                new ArrayList<>(Arrays.asList(new DefaultDataTransformer())),
                new ArrayList<>(Arrays.asList(new CsvDataWriter(), new ParquetDataWriter(), new JdbcDataWriter())));

        Storage input = initializeInput(objectMapper, inputType);
        Storage output = initializeOutput(objectMapper, outputType);

        StorageUtils.updateHadoopConfig(Arrays.asList(input, output), sparkSession.sparkContext().hadoopConfiguration());

        processor.process(input, output);

        return 0;
    }

    public Storage initializeInput(ObjectMapper objectMapper, String type){
        Map<String, String> inputOptionsMap = parseJsonToMap(objectMapper, inputOptions);
        System.out.println(inputOptionsMap);
        if ("csv".equals(type)){
            return CsvStorage.builder()
                    .path(inputPath)
                    .cache(inputCache)
                    .container(inputContainer)
                    .storageAccount(inputStorageAccount)
                    .sasToken(inputSasToken)
                    .options(inputOptionsMap)
                    .build();
        } else if ("parquet".equals(type)) {
            return ParquetStorage.builder()
                    .path(inputPath)
                    .cache(inputCache)
                    .container(inputContainer)
                    .storageAccount(inputStorageAccount)
                    .sasToken(inputSasToken)
                    .options(inputOptionsMap)
                    .build();
        }
        else
            throw new RuntimeException("Type is still not supported : " + type);
    }

    public Storage initializeOutput(ObjectMapper objectMapper, String type){
        Map<String, String> outputOptionsMap = parseJsonToMap(objectMapper, outputOptions);
        if ("csv".equals(type)){
            return CsvStorage.builder()
                    .path(outputPath)
                    .cache(outputCache)
                    .container(outputContainer)
                    .storageAccount(outputStorageAccount)
                    .sasToken(outputSasToken)
                    .options(outputOptionsMap)
                    .build();
        } else if ("parquet".equals(type)) {
            return ParquetStorage.builder()
                    .path(outputPath)
                    .cache(outputCache)
                    .container(outputContainer)
                    .storageAccount(outputStorageAccount)
                    .sasToken(outputSasToken)
                    .options(outputOptionsMap)
                    .build();
        }
        else
            throw new RuntimeException("Type is still not supported : " + type);
    }

    private Map<String, String> parseJsonToMap(ObjectMapper objectMapper, String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, String>>() {});
        } catch (Exception e) {
            // Handle parsing exception or return an empty map if JSON is null or invalid
            return new HashMap<>();
        }
    }
}
