# navigate to the project root directory
# cd /Users/nassimbouali/MyRepos/Data_Spark_DF_Engine_Java
# bash Build/local/buildAndRun.sh

# build docker image
docker build -t spark-df-batch-processing-java -f Build/local/dockerfile .

# run docker container
docker run \
-v /Users/nassimbouali/MyRepos/Data_Spark_DF_Engine_Java/Data_Spark_DF_Application/src/test/resources/data/input:/data/input \
-v /Users/nassimbouali/MyRepos/Data_Spark_DF_Engine_Java/Data_Spark_DF_Application/src/test/resources/data/output:/data/output \
-it spark-df-batch-processing-java \
spark-submit \
--class com.nassim.data.spark.application.Application /app/distribution/data-spark-df-application.jar \
--input-type csv \
--input-path "/data/input/input_data1.csv" \
--input-options "{\"header\":\"true\",\"delimiter\":\";\"}" \
--output-type csv \
--output-path "/data/output/files" \
--output-options "{\"header\":\"true\",\"delimiter\":\";\"}"
