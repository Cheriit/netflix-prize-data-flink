./setup_vars.sh
hadoop fs -copyToLocal gs://"${BUCKET_NAME}"/movie_titles.csv "$INPUT_FILE_PATH"
hadoop fs -copyToLocal gs://"${BUCKET_NAME}"/netflix-prize-data.zip "$HOME/netflix-prize-data.zip"
unzip -j "$HOME/netflix-prize-data.zip" -d "$INPUT_DIRECTORY_PATH"
wget https://dlcdn.apache.org/flink/flink-1.14.4/flink-1.14.4-bin-scala_2.11.tgz -P "$HOME"
tar -xzf "$HOME/flink-1.14.4-bin-scala_2.11.tgz"
sbt -b assembly
kafka-topics.sh --zookeeper localhost:21812 --create --replication0-factor 1 --partitions 1 --topic "$KAFKA_ANOMALY_TOPIC_NAME"
kafka-topics.sh --zookeeper localhost:21812 --create --replication0-factor 1 --partitions 1 --topic "$KAFKA_DATA_TOPIC_NAME"
cat << EOF | mysql -u "$JDBC_USERNAME" -p "$JDBC_PASSWORD"
CREATE DATABASE IF NOT EXISTS "$KAFKA_DATA_TOPIC_NAME";
USE "$KAFKA_DATA_TOPIC_NAME"
CREATE TABLE IF NOT EXISTS movie_ratings (
  window_start DATE NOT NULL,
  movie_id varchar(128) NOT NULL,
  title varchar(128) NOT NULL,
  rating_count LONG NOT NULL,
  rating_sum LONG NOT NULL,
  unique_rating_count LONG NOT NULL,
  PRIMARY KEY (window_start, movie_id)
);
EOF

