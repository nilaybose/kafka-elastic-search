### Class: `ESDataLoader`
**Package:** `nb.edu.kafkaes.es`

#### File Summary
This class acts as a data loader for Elasticsearch, consuming JSON messages from a Kafka topic and indexing or updating them as documents in an Elasticsearch index.

#### Class Summary
`ESDataLoader` orchestrates the process of transferring data from Kafka to Elasticsearch. It manages Kafka consumers and an Elasticsearch `RestHighLevelClient`, using a thread pool to concurrently process messages and perform upsert operations on Elasticsearch documents, ensuring data is continuously synchronized.

#### Imports (Relevant)
- `com.fasterxml.jackson.databind.ObjectMapper` — Used for JSON processing (declared as a field, though not directly used in the provided methods).
- `nb.edu.kafkaes.util.DemoUtilities` — Utility class for obtaining Kafka consumer instances and the Elasticsearch `RestHighLevelClient`.
- `org.apache.kafka.clients.consumer.ConsumerRecord` — Represents a single record consumed from Kafka.
- `org.apache.kafka.clients.consumer.ConsumerRecords` — A collection of records returned by a Kafka consumer poll operation.
- `org.apache.kafka.clients.consumer.KafkaConsumer` — The client for consuming records from Kafka.
- `org.elasticsearch.action.index.IndexRequest` — Represents a request to index a document in Elasticsearch.
- `org.elasticsearch.action.update.UpdateRequest` — Represents a request to update a document in Elasticsearch, used here for upsert functionality.
- `org.elasticsearch.client.RequestOptions` — Provides options for Elasticsearch client requests.
- `org.elasticsearch.client.RestHighLevelClient` — The high-level REST client for interacting with an Elasticsearch cluster.
- `org.elasticsearch.common.xcontent.XContentType` — Specifies the content type of the source, e.g., JSON.
- `java.time.Duration` — Used to specify the timeout for Kafka consumer poll operations.
- `java.util.concurrent.ExecutorService` — Manages a pool of threads for executing tasks concurrently.
- `java.util.concurrent.Executors` — Factory and utility methods for `ExecutorService`.
- `java.util.concurrent.TimeUnit` — Represents time durations at a given unit of granularity.
- `java.util.concurrent.atomic.AtomicBoolean` — A boolean value that may be updated atomically, used for thread-safe shutdown signaling.

#### Metadata
- File: `src/main/java/nb/edu/kafkaes/es/ESDataLoader.java`
- Type: class
- Modifiers: public
- Annotations: None
- Extends: None
- Implements: None

#### Fields
- `mapper`: `ObjectMapper` — Jackson `ObjectMapper` instance for JSON serialization/deserialization.
- `shutdown`: `AtomicBoolean` — A flag to signal the consumer threads to initiate a graceful shutdown.
- `service`: `ExecutorService` — A fixed-size thread pool (3 threads) used to run multiple `getEsConsumer` tasks concurrently.
- `client`: `RestHighLevelClient` — The Elasticsearch `RestHighLevelClient` instance used for interacting with Elasticsearch.

#### Constructors
- `ESDataLoader()` — Default constructor. Initializes the `ObjectMapper`, `AtomicBoolean` shutdown flag, and `ExecutorService` with 3 threads.

#### Methods
- `init(): void`
  - **Summary**: Initializes the Elasticsearch `RestHighLevelClient` and starts multiple concurrent consumer threads to load data into Elasticsearch.
  - **Business Logic**:
    1. Obtains an Elasticsearch `RestHighLevelClient` instance using `DemoUtilities.getEsClient()`.
    2. Submits three instances of the `getEsConsumer` `Runnable` to the `ExecutorService`. Each `Runnable` will run in a separate thread, allowing for concurrent consumption of Kafka messages and indexing into Elasticsearch.
  - **External calls**: `DemoUtilities.getEsClient()`, `ExecutorService.submit()`.
  - **DB interactions**: None directly.
  - **Concurrency / Security / Caching**: Manages the creation and submission of concurrent tasks to an `ExecutorService`.
  - **Javadoc summary (if available)**: Not available.
  - **Testable behavior**:
    - Verify that the `client` field is initialized.
    - Verify that three `Runnable` tasks are submitted to the `ExecutorService`.

- `getEsConsumer(final String id): Runnable`
  - **Summary**: Returns a `Runnable` task that continuously consumes messages from a Kafka topic, processes them, and performs upsert operations (update or insert) on Elasticsearch documents.
  - **Business Logic**:
    1. Creates a `KafkaConsumer` instance for the "active-orders-es" topic, using "es-group" as the consumer group ID.
    2. Enters a `while` loop that continues as long as the `shutdown` flag is `false`.
    3. Inside the loop, it polls the Kafka topic for records with a timeout of 1 second.
    4. If records are received:
       - Iterates through each `ConsumerRecord`:
         - Creates an `IndexRequest` for the "orders" index, using the record key as the document ID and the record value (JSON string) as the source.
         - Creates an `UpdateRequest` for the "orders" index, using the record key as the document ID, the record value (JSON string) as the document content, and specifies the `IndexRequest` as the upsert source (meaning if the document doesn't exist, it will be indexed; otherwise, it will be updated).
         - Executes the `update` operation on the Elasticsearch client with default request options.
         - Prints a message indicating the indexed document ID.
       - Commits the consumer offsets synchronously after processing all records in the batch.
    5. Includes error handling for general exceptions during polling and processing, printing an error message and pausing for 3 seconds before retrying.
    6. Closes the `KafkaConsumer` when the `shutdown` flag becomes `true`.
  - **External calls**: `DemoUtilities.getConsumer()`, `KafkaConsumer.poll()`, `IndexRequest` constructor, `UpdateRequest` constructor, `RestHighLevelClient.update()`, `KafkaConsumer.commitSync()`, `KafkaConsumer.close()`.
  - **DB interactions**: None directly.
  - **Concurrency / Security / Caching**: Designed to run concurrently in a separate thread. Includes exception handling and a graceful shutdown mechanism.
  - **Javadoc summary (if available)**: Not available.
  - **Testable behavior**:
    - Verify that the consumer correctly polls messages from the input topic.
    - Verify that `IndexRequest` and `UpdateRequest` objects are correctly constructed for upsert operations.
    - Verify that `client.update()` is invoked for each processed record.
    - Verify that consumer offsets are committed.
    - Verify the graceful shutdown behavior of the consumer loop.

- `shutdown(): void`
  - **Summary**: Initiates a graceful shutdown of the `ESDataLoader`, stopping all consumer threads and closing the Elasticsearch client.
  - **Business Logic**:
    1. Sets the `shutdown` `AtomicBoolean` flag to `true`, which signals all running `getEsConsumer` threads to terminate their processing loops.
    2. Initiates a graceful shutdown of the `ExecutorService`, preventing new tasks from being submitted and allowing existing tasks to complete.
    3. Waits for the `ExecutorService` to terminate for up to 30 seconds, allowing active tasks to finish.
    4. Closes the Elasticsearch `RestHighLevelClient`, releasing its resources. Includes a `try-catch` block to ignore exceptions during client closure.
  - **External calls**: `ExecutorService.shutdown()`, `ExecutorService.awaitTermination()`, `RestHighLevelClient.close()`.
  - **DB interactions**: None directly.
  - **Concurrency / Security / Caching**: Manages the graceful shutdown of a thread pool and the Elasticsearch client.
  - **Javadoc summary (if available)**: Not available.
  - **Testable behavior**:
    - Verify that the `shutdown` flag is set to `true`.
    - Verify that `ExecutorService.shutdown()` is called.
    - Verify that `ExecutorService.awaitTermination()` is called with the specified timeout.
    - Verify that `client.close()` is invoked.

#### Relationships
- Associations: None directly.
- DI/service dependencies:
  - Depends on `DemoUtilities` for Kafka consumer and Elasticsearch client instances.
  - Uses `ObjectMapper` (though not directly in methods provided).
  - Manages `RestHighLevelClient` and `KafkaConsumer` instances.

#### Database Layer
None directly. This class interacts with Kafka and Elasticsearch, not a traditional relational database.

#### External Interactions
- HTTP endpoints called: Interacts with Elasticsearch via its REST API.
- Messaging topics/queues: Consumes from Kafka topic: "active-orders-es".
- Filesystem/caches: None directly.

#### Dependencies
- `com.fasterxml.jackson.databind:jackson-databind`
- `org.apache.kafka:kafka-clients`
- `org.elasticsearch.client:elasticsearch-rest-high-level-client`
- `org.elasticsearch:elasticsearch`
- `nb.edu.kafkaes.util.DemoUtilities`

#### Links
- Related classes:
  - [DemoUtilities](specs/src/main/java/nb/edu/kafkaes/util/DemoUtilities.md)

#### Notes
- The `ObjectMapper` field is declared but not directly used within the provided methods of this class.
