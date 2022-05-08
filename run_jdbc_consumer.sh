source ./setup_vars.sh
java -cp $(pwd)/target/scala-2.11/*.jar com.cherit.consumers.JdbcRecordConsumer \
  "$JDBC_URL" \
  "$JDBC_USERNAME" \
  "$JDBC_PASSWORD"
