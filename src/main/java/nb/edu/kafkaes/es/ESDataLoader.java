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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ESDataLoader {
    private final ObjectMapper mapper = new ObjectMapper();
    private final AtomicBoolean shutdown = new AtomicBoolean(false);
    private final ExecutorService service = Executors.newFixedThreadPool(3);
    private RestHighLevelClient client;

    public void init() {
        client = DemoUtilities.getEsClient();

        for (int i = 0; i < 3; i++) {
            service.submit(getEsConsumer(String.valueOf(i)));
        }
    }

    private Runnable getEsConsumer(final String id) {
        return () -> {
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
        };
    }

    public void shutdown() {
        shutdown.set(true);
        service.shutdown();
        try {
            service.awaitTermination(30, TimeUnit.SECONDS);
        } catch (Exception ex) {
            //ignore
        }
        try {
            client.close();
        } catch (Exception ex) {
            //ignore
        }
    }
}
