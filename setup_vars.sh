# Cloud parameters
export BUCKET_NAME="<your bucket name>"
export CLUSTER_NAME=$(/usr/share/google/get_metadata_value attributes/dataproc-cluster-name)
export HADOOP_CONF_DIR=/etc/hadoop/conf
export HADOOP_CLASSPATH=`hadoop classpath`
export INPUT_DIRECTORY_PATH="$HOME/netflix-prize-data"
export INPUT_FILE_PATH="$HOME/movie_titles.csv"

# Kafka parameters
export KAFKA_PRODUCER_SLEEP_TIME=15
export KAFKA_DATA_TOPIC_NAME="netflix-ratings"
export KAFKA_ANOMALY_TOPIC_NAME="netflix-ratings-anomalies"
export KAFKA_BOOTSTRAP_SERVERS="${CLUSTER_NAME}-w-0:9092"
export KAFKA_GROUP_ID="netflix-ratings-group"

# JDBC parameters
export JDBC_URL=""
export JDBC_DRIVER_NAME=""
export JDBC_USERNAME="root"
export JDBC_PASSWORD=""

# Flink parameters
export FLINK_DIRECTORY="$HOME/flink-1.14.4-bin-scala_2.11"

# Processing Engine parameters
export ANOMALY_PERIOD_LENGTH="30"
export ANOMALY_RATING_COUNT="100"
export ANOMALY_RATING_MEAN="4"
export PROCESSING_TYPE="H" # H for historical, S for Stream