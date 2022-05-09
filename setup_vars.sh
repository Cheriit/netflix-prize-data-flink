# Cloud parameters
export BUCKET_NAME="fsz-pbd-2022"
export CLUSTER_NAME=$(/usr/share/google/get_metadata_value attributes/dataproc-cluster-name)
export HADOOP_CONF_DIR=/etc/hadoop/conf
export HADOOP_CLASSPATH=`hadoop classpath`
export INPUT_DIRECTORY_PATH="$HOME/netflix-prize-data"
export INPUT_FILE_PATH="$HOME/movie_titles.csv"

# Kafka parameters
export KAFKA_PRODUCER_SLEEP_TIME=30
export KAFKA_DATA_TOPIC_NAME="netflix-ratings"
export KAFKA_ANOMALY_TOPIC_NAME="netflix-ratings-anomalies"
export KAFKA_BOOTSTRAP_SERVERS="${CLUSTER_NAME}-w-0:9092"
export KAFKA_GROUP_ID="netflix-ratings-group"

# JDBC parameters
export JDBC_URL="jdbc:mysql://${CLUSTER_NAME}-m:3306/netflix_ratings"
export JDBC_USERNAME="streamuser"
export JDBC_PASSWORD="stream"

# Flink parameters
export FLINK_DIRECTORY="$HOME/netflix-prize-data-flink/flink-1.14.4"

# Processing Engine parameters
export ANOMALY_PERIOD_LENGTH=30
export ANOMALY_RATING_COUNT=70
export ANOMALY_RATING_MEAN=4
export PROCESSING_TYPE="H" # H for historical, S for Stream