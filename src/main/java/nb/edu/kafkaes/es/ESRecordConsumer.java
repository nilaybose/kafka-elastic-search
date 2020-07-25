package nb.edu.kafkaes.es;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import nb.edu.kafkaes.util.DemoDataSource;
import nb.edu.kafkaes.util.KafkaUtilities;
import nb.edu.kafkaes.vo.CustomerRecord;
import nb.edu.kafkaes.vo.ESRecord;
import nb.edu.kafkaes.vo.OrderProducts;
import nb.edu.kafkaes.vo.OrderRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Producer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ESRecordConsumer implements Runnable {
    private ObjectMapper mapper = new ObjectMapper();
    private AtomicBoolean shutdown;
    private String id;

    public ESRecordConsumer(String id,
                            AtomicBoolean shutdown) {
        this.id = id;
        this.shutdown = shutdown;
    }

    @Override
    public void run() {
        KafkaConsumer<String, String> consumer
                = KafkaUtilities.getConsumer(id, "esgroup4", "active-orders-es");
        while (!shutdown.get()) {
            try {
                //Poll and read from kafka topic
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                if (!records.isEmpty()) {
                    for (ConsumerRecord<String, String> record : records) {
                        System.out.printf("ES record consumer [%s], offset [%d], value [%s]\n", id, record.offset(), record.value());
                    }
                    consumer.commitSync();
                }
            } catch (Exception ex) {
                System.out.println("Exception in ESRecordConsumer - " + ex.getMessage());
                try {
                    Thread.sleep(3000L);
                } catch (Exception ignore) {
                    //ignore
                }
            }
        }
    }
}
