package nb.edu.kafkaes.connect;

import nb.edu.kafkaes.es.ESDataLoader;

public class ConnectDemoApp {
    public static void main(String[] args) {
        ConnectOrderDataLoader dataLoader = new ConnectOrderDataLoader();
        new Thread(dataLoader).start();

        ConnectESDataGenerator esDataGenerator = new ConnectESDataGenerator();
        esDataGenerator.init();

        Runtime.getRuntime().
                addShutdownHook(new Thread(() -> {
                    System.out.println("Shutdown Hook is running !");
                    dataLoader.shutdown();
                }));
    }
}
