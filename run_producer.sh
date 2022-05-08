source ./setup_vars.sh
java -cp ../producers/target/scalka-2.12/KafkaRecordProducer.jar com.cherit.producer.KafkaProducer \
  "$INPUT_DIRECTORY" \
  "$KAFKA_PRODUCER_SLEEP_TIME" \
  "$KAFKA_DATA_TOPIC_NAME" \
  "$KAFKA_BOOTSTRAP_SERVERS"