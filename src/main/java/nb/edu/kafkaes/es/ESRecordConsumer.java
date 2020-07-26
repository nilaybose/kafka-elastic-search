package nb.edu.kafkaes.es;

import com.fasterxml.jackson.databind.ObjectMapper;
import nb.edu.kafkaes.util.DemoUtilities;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

public class ESRecordConsumer implements Runnable {
    private ObjectMapper mapper = new ObjectMapper();
    private AtomicBoolean shutdown;
    private RestHighLevelClient client;
    private String id;

    public ESRecordConsumer(String id,
                            AtomicBoolean shutdown,
                            RestHighLevelClient client) {
        this.id = id;
        this.shutdown = shutdown;
        this.client = client;
    }

    @Override
    public void run() {
        KafkaConsumer<String, String> consumer
                = DemoUtilities.getConsumer(id, "es-group", "active-orders-es");
        while (!shutdown.get()) {
            try {
                //Poll and read from kafka topic
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                if (!records.isEmpty()) {
                    for (ConsumerRecord<String, String> record : records) {
                        IndexRequest indexRequest = new IndexRequest("orders")
                                .id(record.key())
                                .source(record.value(), XContentType.JSON);

                        UpdateRequest updateRequest = new UpdateRequest("orders", record.key())
                                .doc(record.value(), XContentType.JSON)
                                .upsert(indexRequest);
                        client.update(updateRequest, RequestOptions.DEFAULT).getId();
                        System.out.printf("ESRecordConsumer [%s]: indexed id [%s]\n", id, record.key());
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
        consumer.close();
    }
}
