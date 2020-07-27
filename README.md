# kafka-elastic-search
Pull data from rdbms and push data to es using kafka topics/ connect

####Custom kafka consumer
![Approach - 1](approach1.jpg)

####Using kafka connect
![Approach - 2](approach2.jpg)

###Kafka Connect JDBC
cd ./connect
alias kconnect='${PATH_2_KAFKA}/bin/connect-standalone.sh ./worker.properties ./connect.properties'
