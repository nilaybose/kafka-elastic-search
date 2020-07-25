package nb.edu.kafkaes.app;

import nb.edu.kafkaes.sql.OrderDataLoader;

public class DemoApplication {
    public static void main(String[] args) throws Exception{
        OrderDataLoader.produceOrders();
    }
}
