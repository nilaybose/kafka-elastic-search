package nb.edu.kafkaes.sql;

import com.fasterxml.jackson.databind.ObjectMapper;
import nb.edu.kafkaes.kafka.KafkaUtilities;
import nb.edu.kafkaes.vo.KafkaOrderRecord;
import org.apache.kafka.clients.producer.Producer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Random;
import java.util.UUID;

public class OrderDataLoader {
    private static Producer<String, String> producer;
    private static ObjectMapper mapper = new ObjectMapper();

    public static void produceOrders() throws Exception {
        //Only one instance of kafka producer must be created
        //it is thread safe
        //We can create the active-order topic as "kafka-topics.sh --create --zookeeper 127.0.0.1:2181 --partitions 3 --replication-factor 1 --topic active-orders"
        producer = KafkaUtilities.getProducer();
        createOrder("a44d3eb4-24ec-42e3-bec6-454165592515");
    }

    public static void loadCustomer() throws Exception {
        String sql = "INSERT INTO kafka.customers (ID,address,region,name) VALUES (?, ?, ?, ? );";
        try (Connection conn = DemoDataSource.getConnection();
             PreparedStatement ps = conn.prepareCall(sql)) {
            ps.setObject(1, UUID.randomUUID());
            ps.setString(2, "KA");
            ps.setString(3, "india");
            ps.setString(4, "John");
            ps.execute();
            conn.commit();
        }
    }

    private static String createOrder(String customer) throws Exception {
        String order = "INSERT INTO kafka.orders(ID,cust_id,total,ts_placed, description) VALUES (?, ?, ?, ?, ?)";
        String odrProducts = "INSERT INTO kafka.orders_products (ID,order_id,product_id) VALUES (?, ?, ?)";
        String orderId = UUID.randomUUID().toString();
        try (Connection conn = DemoDataSource.getConnection();
             PreparedStatement psOrders = conn.prepareCall(order);
             PreparedStatement psItems = conn.prepareCall(odrProducts)) {

            try {
                int total = new Random().nextInt(100) + 1;

                psOrders.setString(1, orderId);
                psOrders.setString(2, customer);
                psOrders.setString(3, "" + total);
                psOrders.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                psOrders.setString(5, "Desc_" + total);
                psOrders.execute();

                psItems.setString(1, UUID.randomUUID().toString());
                psItems.setString(2, orderId);
                psItems.setString(3, "tv_" + orderId);
                psItems.addBatch();

                psItems.setString(1, UUID.randomUUID().toString());
                psItems.setString(2, orderId);
                psItems.setString(3, "cycle_" + orderId);
                psItems.addBatch();

                psItems.setString(1, UUID.randomUUID().toString());
                psItems.setString(2, orderId);
                psItems.setString(3, "book_" + orderId);
                psItems.addBatch();

                psItems.executeBatch();
                conn.commit();
                //kafka produce must happen after db commit
                KafkaUtilities.sendToTopic(producer, "active-orders", orderId,
                        mapper.writeValueAsString(new KafkaOrderRecord(orderId, "INSERT")));

            } catch (Exception ex) {
                conn.rollback();
            }
        }
        return orderId;
    }
}

