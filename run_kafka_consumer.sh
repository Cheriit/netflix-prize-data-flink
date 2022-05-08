source ./setup_vars.sh
java -cp consumers/target/scala-2.12/consumers.jar com.cherit.consumers.KafkaRecordConsumer \
  "$KAFKA_BOOTSTRAP_SERVERS" \
  "$KAFKA_GROUP_ID" \
  "$KAFKA_ANOMALY_TOPIC_NAME"