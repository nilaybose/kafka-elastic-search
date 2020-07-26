package nb.edu.kafkaes.app;

import nb.edu.kafkaes.sql.ActiveOrderDataLoader;

public class DemoRDMSLoaderApp {
    public static void main(String[] args) {
        ActiveOrderDataLoader dataLoader = new ActiveOrderDataLoader();
        new Thread(dataLoader).start();

        Runtime.getRuntime().
                addShutdownHook(new Thread(() -> {
                    System.out.println("Shutdown Hook is running !");
                    dataLoader.shutdown();
                }));
    }
}
