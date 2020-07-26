# kafka-elastic-search
Pull data from rdbms and push data to es using kafka topic

![Approach](approach.jpg)

###Kafka Connect JDBC
cd ./connect
connect-standalone.sh ./connect/worker.properties ./connect/db.properties
