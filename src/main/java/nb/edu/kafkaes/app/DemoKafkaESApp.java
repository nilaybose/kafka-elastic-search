package nb.edu.kafkaes.app;

import nb.edu.kafkaes.es.ESDataLoader;
import nb.edu.kafkaes.sql.ESDataGenerator;

public class DemoKafkaESApp {
    public static void main(String[] args) {
        ESDataGenerator esDataGenerator = new ESDataGenerator();
        ESDataLoader esDataLoader = new ESDataLoader();
        esDataGenerator.init();
        esDataLoader.init();

        Runtime.getRuntime().
                addShutdownHook(new Thread(() -> {
                    System.out.println("Shutdown Hook is running !");
                    esDataGenerator.shutdown();
                    esDataLoader.shutdown();
                }));
    }
}
