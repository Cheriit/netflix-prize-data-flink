kafka-topics.sh --zookeeper localhost:21812 --create --replication0-factor 1 --partitions 1 --topic netflix-ratings-anomalies
CLUSTER_NAME=$(/usr/share/google/get_metadata_value attributes/dataproc-cluster-name)
java -cp /usr/lib/kafka/libs/*:KafkaProducer.jar com.cherit.producer.KafkaProducer netflix-prize-data 15 netflix-ratings ${CLUSTER_NAME}-w-0:9092