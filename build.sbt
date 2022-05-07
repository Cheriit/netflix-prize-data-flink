ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.cherit"
ThisBuild / scalaVersion := "2.12.14"
val flinkVersion = "1.14.4"


libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-clients" % "2.3.1",
  "mysql" % "mysql-connector-java" % "8.0.28",
  "org.apache.flink" %% "flink-scala" % flinkVersion,
  "org.apache.flink" %% "flink-streaming-scala" % flinkVersion % "provided",
  "org.apache.flink" %% "flink-connector-kafka" % flinkVersion,
  "org.apache.flink" %% "flink-connector-jdbc" % flinkVersion,
)

lazy val processing_engine = (project in file("processing_engines"))
  .settings(
    assembly / mainClass := Some("com.cherit.processing_engine.ProcessingEngine"),
    assembly / assemblyJarName := "ProcessingEngine.jar"
  )

lazy val consumers = (project in file("consumers"))
  .settings(
    assembly / assemblyJarName := "consumers.jar"
  )

lazy val producers = (project in file("producers"))
  .settings(
    assembly / mainClass := Some("com.cherit.producers.KafkaRecordProducer"),
    assembly / assemblyJarName := "KafkaRecordProducer.jar"
  )