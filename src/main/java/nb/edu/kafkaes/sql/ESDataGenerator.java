package nb.edu.kafkaes.sql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import nb.edu.kafkaes.util.DemoDataSource;
import nb.edu.kafkaes.util.DemoUtilities;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ESDataGenerator {
    private Producer<String, String> producer;
    private final ObjectMapper mapper = new ObjectMapper();
    private final AtomicBoolean shutdown = new AtomicBoolean(false);
    private final ExecutorService service = Executors.newFixedThreadPool(3);

    public void init() {
        producer = DemoUtilities.getProducer();
        for (int i = 0; i < 5; i++) {
            service.submit(getActiveOrderConsumer(String.valueOf(i)));
        }
    }

    Runnable getActiveOrderConsumer(final String id) {
        return () -> {
            KafkaConsumer<String, String> consumer
                    = DemoUtilities.getConsumer(id,
                    System.getProperty("active-orders-group", "order-group"), "active-orders");
            while (!shutdown.get()) {
                try {
                    //Poll and read from kafka topic
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                    if (!records.isEmpty()) {
                        try (Connection conn = DemoDataSource.getConnection()) {
                            for (ConsumerRecord<String, String> record : records) {
                                //Read the kafka json as Order
                                OrderRecord kafkaOrder = mapper.readValue(record.value(), OrderRecord.class);

                                //Read data base to generate the elastic search document
                                OrderRecord orderRecord = getOrderRecord(conn, kafkaOrder.getOrderId());
                                if (orderRecord != null) {
                                    CustomerRecord customerRecord = getCustomerRecord(conn, orderRecord.getCustId());
                                    List<OrderProducts> products = getOrderProducts(conn, kafkaOrder.getOrderId());
                                    ESRecord esRecord = new ESRecord(orderRecord, customerRecord, products);

                                    //Write to elastic search topic
                                    DemoUtilities.sendToTopic(producer,
                                            "active-orders-es",
                                            kafkaOrder.getOrderId(),
                                            mapper.writeValueAsString(esRecord), true);
                                }
                            }
                        }
                        consumer.commitSync();
                    }
                } catch (Exception ex) {
                    System.out.println("Exception in ActiveOrderConsumer - " + ex.getMessage());
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

    OrderRecord getOrderRecord(Connection conn, String orderId) throws Exception {
        OrderRecord record = null;
        String orderSql = "Select id, cust_id, total, ts_placed, description from demo.orders where id = ?";
        try (PreparedStatement ps = conn.prepareCall(orderSql)) {
            ps.setString(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    record = new OrderRecord(
                            orderId,
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4),
                            rs.getString(5));
                }
            }
        }
        return record;
    }

    @VisibleForTesting
    CustomerRecord getCustomerRecord(Connection conn, String customerId) throws Exception {
        CustomerRecord record = null;
        String orderSql = "select id, address, region, name from demo.customers where id = ?";
        try (PreparedStatement ps = conn.prepareCall(orderSql)) {
            ps.setString(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    record = new CustomerRecord(
                            customerId,
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4));
                }
            }
        }
        return record;
    }

    @VisibleForTesting
    List<OrderProducts> getOrderProducts(Connection conn, String orderId) throws Exception {
        OrderProducts record;
        List<OrderProducts> products = new ArrayList<>();
        String orderSql = "select id, product_id from demo.orders_products where order_id = ?";
        try (PreparedStatement ps = conn.prepareCall(orderSql)) {
            ps.setString(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    record = new OrderProducts(
                            rs.getString(1),
                            orderId,
                            rs.getString(2));
                    products.add(record);
                }
            }
        }
        return products;
    }

    public void shutdown() {
        shutdown.set(true);
        service.shutdown();
        try {
            service.awaitTermination(15, TimeUnit.SECONDS);
        } catch (Exception ex) {
            //ignore
        }
        producer.flush();
        producer.close();
    }
}
