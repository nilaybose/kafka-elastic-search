package nb.edu.kafkaes.util;

import org.apache.http.HttpHost;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.*;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.Future;

public class DemoUtilities {
    public static Producer<String, String> getProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.setProperty(ProducerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, "3000");
        props.setProperty(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, "1000");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 5);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        return new KafkaProducer<>(props);
    }

    public static KafkaConsumer<String, String> getConsumer(String id, String groupId, String topic) {
        Properties props = new Properties();
        props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, groupId + "_" + id);
        props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "50");
        props.setProperty(ConsumerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, "3000");
        props.setProperty(ConsumerConfig.RECONNECT_BACKOFF_MS_CONFIG, "1000");
        props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList(topic));
        return consumer;
    }

    public static RestHighLevelClient getEsClient() {
        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));
    }

    public static void sendToTopic(Producer<String, String> producer,
                                   final String topic, String key, String value, boolean sync) throws Exception {
        Callback callback = (metadata, exception) -> {
            if (exception == null) {
                System.out.println(String.format("Published record to kafka topic [%s], partition [%d], offset [%d]",
                        topic, metadata.partition(), metadata.offset()));
            } else {
                System.out.println(
                        String.format("Error producing to kafka topic [%s], message [%s]",
                                topic,
                                exception.getMessage()));
            }
        };
        Future<RecordMetadata> meta
                = producer.send(
                new ProducerRecord<>(topic, key, value),
                callback);
        if (sync) {
            meta.get();
        }
        producer.flush();
    }
}
