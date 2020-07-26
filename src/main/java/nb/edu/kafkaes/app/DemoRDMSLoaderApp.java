package nb.edu.kafkaes.app;

import nb.edu.kafkaes.sql.OrderDataLoader;

public class DemoRDMSLoaderApp {
    public static void main(String[] args) {
        OrderDataLoader dataLoader = new OrderDataLoader();
        new Thread(dataLoader).start();

        Runtime.getRuntime().
                addShutdownHook(new Thread(() -> {
                    System.out.println("Shutdown Hook is running !");
                    dataLoader.shutdown();
                }));
    }
}
