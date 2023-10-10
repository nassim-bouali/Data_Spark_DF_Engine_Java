# Batch Data Processing Engine

# Introduction
This document describes a data processing engine used to collect data in batches from different sources, process that data and then write it down towards one of many possible sinks. The engine is built with Java on top of the Spark framework using its DataFrame API.

# 1) Architecture
![Batch Processing Architecture](diagrams/Architecture_Batch_DataFrame_Processing_Engine.drawio.png "Batch Processing Architecture Diagram")

## 2) Data Processing Engine
The engine is a Spark application that enables reading data from different types of inputs:
- CSV
- Parquet
- SQL Table
- Azure Data Lake Storage Blobs

Later in the process, the input read data gets transformed and then could be written to the following set of outputs:
- CSV
- Parquet
- SQL Table
- Azure Data Lake Storage Blobs

Keep in mind that this engine is built on top of the Spark DataFrame API. Hence, any transformation logic gets handled within the application code itself.

As of the latest version, one default transformation logic is supported and does nothing else than keeping the input data as of the same format. Any additional and/or specific transformations must be implemented by the development team.
Additionally, configuring the application's data flow relies on program arguments. Samples can be found in the [Program Arguments Samples Directory](Program Arguments Samples)