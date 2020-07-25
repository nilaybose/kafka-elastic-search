package nb.edu.kafkaes.sql;

import com.fasterxml.jackson.databind.ObjectMapper;
import nb.edu.kafkaes.util.KafkaUtilities;
import org.apache.kafka.clients.producer.Producer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ESDataGenerator {
    private Producer<String, String> producer;
    private ObjectMapper mapper = new ObjectMapper();
    private AtomicBoolean shutdown = new AtomicBoolean(false);
    private ExecutorService service =  Executors.newFixedThreadPool(3);

    public void init() {
        producer = KafkaUtilities.getProducer();
        for(int i = 0; i < 3; i++){
            service.submit(new ActiveOrderConsumer(String.valueOf(i), producer, shutdown));
        }
    }

    public void shutdown() {
        shutdown.set(true);
        service.shutdown();
        try{
            service.awaitTermination(15, TimeUnit.SECONDS);
        }
        catch (Exception ex){
            //ignore
        }
        producer.flush();
        producer.close();
    }
}
