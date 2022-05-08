./setup_vars.sh
sudo apt-get install sbt
hadoop fs -copyToLocal gs://"${BUCKET_NAME}"/movie_titles.csv "$INPUT_FILE_PATH"
hadoop fs -copyToLocal gs://"${BUCKET_NAME}"/netflix-prize-data.zip "$HOME/netflix-prize-data.zip"
unzip -j "$HOME/netflix-prize-data.zip" -d "$INPUT_DIRECTORY_PATH"
wget https://dlcdn.apache.org/flink/flink-1.14.4/flink-1.14.4-bin-scala_2.11.tgz -P "$HOME"
tar -xzf "$HOME/flink-1.14.4-bin-scala_2.11.tgz"
sbt -b assembly
kafka-topics.sh --zookeeper localhost:21812 --create --replication0-factor 1 --partitions 1 --topic "$KAFKA_ANOMALY_TOPIC_NAME"
kafka-topics.sh --zookeeper localhost:21812 --create --replication0-factor 1 --partitions 1 --topic "$KAFKA_DATA_TOPIC_NAME"
docker run -p 3306:3306 -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE="$KAFKA_DATA_TOPIC_NAME" -e MYSQL_ADMIN="$JDBC_USERNAME" -e MQL_PASSWORD="$JDBC_PASSWORD" -v "$(pwd)"/setup.sql:/docker-entrypoint-initdb.d/setup.sql:ro -d mysql:latest

