ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.cherit"
ThisBuild / scalaVersion := "2.11.12"
val flinkVersion = "1.14.4"


libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-clients" % "2.3.1",
  "mysql" % "mysql-connector-java" % "8.0.28",
  "org.apache.flink" % "flink-core" % flinkVersion,
  "org.apache.flink" %% "flink-scala" % flinkVersion,
  "org.apache.flink" %% "flink-streaming-scala" % flinkVersion,
  "org.apache.flink" %% "flink-connector-kafka" % flinkVersion,
  "org.apache.flink" %% "flink-connector-jdbc" % flinkVersion,
)