source ./setup_vars.sh
echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | sudo tee /etc/apt/sources.list.d/sbt.list
echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | sudo tee /etc/apt/sources.list.d/sbt_old.list
curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | sudo apt-key add
sudo apt-get update
sudo apt-get install sbt
hadoop fs -copyToLocal gs://"${BUCKET_NAME}"/movie_titles.csv "$INPUT_FILE_PATH"|| exit
hadoop fs -copyToLocal gs://"${BUCKET_NAME}"/netflix-prize-data.zip "$HOME/netflix-prize-data.zip" || exit
unzip -j "$HOME/netflix-prize-data.zip" -d "$INPUT_DIRECTORY_PATH" || exit
wget https://dlcdn.apache.org/flink/flink-1.14.4/flink-1.14.4-bin-scala_2.11.tgz -P "$HOME" || exit
tar -xzf "$HOME/flink-1.14.4-bin-scala_2.11.tgz" || exit
sbt clean package || exit
kafka-topics.sh --zookeeper localhost:2181 --create --replication-factor 1 --partitions 1 --topic "$KAFKA_ANOMALY_TOPIC_NAME" || exit
kafka-topics.sh --zookeeper localhost:2181 --create --replication-factor 1 --partitions 1 --topic "$KAFKA_DATA_TOPIC_NAME" || exit
docker run -p 3307:3306 -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE="$KAFKA_DATA_TOPIC_NAME" -e MYSQL_ADMIN="$JDBC_USERNAME" -e MQL_PASSWORD="$JDBC_PASSWORD" -v "$(pwd)"/setup.sql:/docker-entrypoint-initdb.d/setup.sql:ro -d mysql:latest || exit

