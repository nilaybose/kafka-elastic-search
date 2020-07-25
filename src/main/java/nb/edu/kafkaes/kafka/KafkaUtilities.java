package nb.edu.kafkaes.kafka;

import org.apache.kafka.clients.producer.*;

import java.util.Properties;
import java.util.concurrent.Future;

public class KafkaUtilities {
    public static Producer<String, String> getProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("retries", 5);
        props.put("linger.ms", 1);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return new KafkaProducer<>(props);
    }

    public static void sendToTopic(Producer<String, String> producer, String topic, String key, String value) {
        Callback callback = (metadata, exception) -> {
            if (exception == null) {
                System.out.println("Published record to kafka - " + metadata.partition() + ":" + metadata.offset());
            } else {
                System.out.println("Error producing to kafka - " + exception.getMessage());
            }
        };
        Future<RecordMetadata> meta
                = producer.send(
                new ProducerRecord<>(topic, key, value),
                callback);
        //meta.get(); //Sync write to kafka
        producer.flush();
    }
}
