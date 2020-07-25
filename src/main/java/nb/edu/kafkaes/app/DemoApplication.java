package nb.edu.kafkaes.app;

import nb.edu.kafkaes.es.ESDataLoader;
import nb.edu.kafkaes.sql.ESDataGenerator;
import nb.edu.kafkaes.sql.ActiveOrderDataLoader;

public class DemoApplication {
    public static void main(String[] args) {
        ActiveOrderDataLoader dataLoader = new ActiveOrderDataLoader();
        ESDataGenerator esDataGenerator = new ESDataGenerator();
        ESDataLoader esDataLoader = new ESDataLoader();
        new Thread(dataLoader).start();
        esDataGenerator.init();
        esDataLoader.init();

        Runtime.getRuntime().
                addShutdownHook(new Thread(() -> {
                    System.out.println("Shutdown Hook is running !");
                    dataLoader.shutdown();
                    esDataGenerator.shutdown();
                    esDataLoader.shutdown();
                }));
    }
}
