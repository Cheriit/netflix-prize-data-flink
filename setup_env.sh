hadoop fs -copyToLocal gs://fsz-pbd-2022/movie_titles.csv
hadoop fs -copyToLocal gs://fsz-pbd-2022/netflix-prize-data.zip
unzip -j netflix-prize-data.zip
wget https://dlcdn.apache.org/flink/flink-1.14.4/flink-1.14.4-bin-scala_2.11.tgz
tar -xzf flink-1.14.4-bin-scala_2.11.tgz
