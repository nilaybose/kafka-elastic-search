package nb.edu.kafkaes.es;

import com.fasterxml.jackson.databind.ObjectMapper;
import nb.edu.kafkaes.util.DemoUtilities;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ESDataLoader {
    private ObjectMapper mapper = new ObjectMapper();
    private AtomicBoolean shutdown = new AtomicBoolean(false);
    private ExecutorService service =  Executors.newFixedThreadPool(3);
    private RestHighLevelClient client;

    public void init() {
        client = DemoUtilities.getEsClient();

        for(int i = 0; i < 3; i++){
            service.submit(new ESRecordConsumer(String.valueOf(i), shutdown, client));
        }
    }

    public void shutdown() {
        shutdown.set(true);
        service.shutdown();
        try{
            service.awaitTermination(15, TimeUnit.SECONDS);
            client.close();
        }
        catch (Exception ex){
            //ignore
        }
    }
}
