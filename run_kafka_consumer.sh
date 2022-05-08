source ./setup_vars.sh
java -cp $(pwd)/target/scala-2.12/*.jar com.cherit.consumers.KafkaRecordConsumer \
  "$KAFKA_BOOTSTRAP_SERVERS" \
  "$KAFKA_GROUP_ID" \
  "$KAFKA_ANOMALY_TOPIC_NAME"