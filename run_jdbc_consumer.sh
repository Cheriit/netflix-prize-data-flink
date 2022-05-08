source ./setup_vars.sh
java -cp $(pwd)/target/scala-2.12/*.jar com.cherit.consumers.JdbcRecordConsumer \
  "$JDBC_URL" \
  "$JDBC_DRIVER_NAME" \
  "$JDBC_USERNAME" \
  "$JDBC_PASSWORD"
