source ./setup_vars.sh
java -cp consumers/target/scala-2.12/consumers.jar com.cherit.consumers.JdbcRecordConsumer \
  "$JDBC_URL" \
  "$JDBC_DRIVER_NAME" \
  "$JDBC_USERNAME" \
  "$JDBC_PASSWORD"
