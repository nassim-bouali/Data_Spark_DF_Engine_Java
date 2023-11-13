# navigate to the project root directory
# cd /path/to/project
# bash Build/local/buildAndRun.sh /path/to/data/input /path/to/data/output

INPUT_DATA_PATH=$1
OUTPUT_DATA_PATH=$2

# build docker image
docker build -t spark-df-batch-processing-java -f Build/local/dockerfile .

# run docker container
docker run \
-v $INPUT_DATA_PATH:/data/input \
-v $OUTPUT_DATA_PATH:/data/output \
-it spark-df-batch-processing-java \
spark-submit \
--class com.nassim.data.spark.application.Application /app/distribution/data-spark-df-application.jar \
--input-type csv \
--input-path "/data/input/input_data1.csv" \
--input-options "{\"header\":\"true\",\"delimiter\":\";\"}" \
--output-type csv \
--output-path "/data/output/files" \
--output-options "{\"header\":\"true\",\"delimiter\":\";\"}"
