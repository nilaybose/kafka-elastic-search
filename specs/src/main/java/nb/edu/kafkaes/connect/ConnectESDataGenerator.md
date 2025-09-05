### Class: `ConnectESDataGenerator`
**Package:** `nb.edu.kafkaes.connect`

#### File Summary
This class is responsible for consuming order data from a Kafka topic, enriching it with customer and product information from a relational database, and then producing a consolidated Elasticsearch-ready document to another Kafka topic.

#### Class Summary
`ConnectESDataGenerator` acts as a bridge between Kafka and Elasticsearch, transforming raw order data into a rich format suitable for indexing. It manages Kafka consumers and producers, interacts with a database for data enrichment, and uses an `ExecutorService` for concurrent processing of multiple consumer threads.

#### Imports (Relevant)
- `com.fasterxml.jackson.databind.ObjectMapper` — Used for converting Java objects (specifically `ESRecord`) into JSON strings for Kafka production.
- `com.google.common.annotations.VisibleForTesting` — Annotation used to mark methods primarily for testing purposes.
- `com.google.gson.Gson` — Used for deserializing JSON messages received from Kafka into `OrderValue` objects.
- `nb.edu.kafkaes.util.DemoDataSource` — Provides database connection pooling for efficient access to the relational database.
- `nb.edu.kafkaes.util.DemoUtilities` — Utility class for obtaining Kafka producers/consumers and sending messages to Kafka topics.
- `nb.edu.kafkaes.vo.CustomerRecord` — Value object representing customer data retrieved from the database.
- `nb.edu.kafkaes.vo.ESRecord` — Value object representing the consolidated data structure for Elasticsearch.
- `nb.edu.kafkaes.vo.OrderProducts` — Value object representing product details associated with an order.
- `nb.edu.kafkaes.vo.OrderRecord` — Value object representing order details retrieved from the database.
- `nb.edu.kafkaes.connect.OrderValue` — Value object representing the structure of order messages consumed from Kafka.
- `org.apache.kafka.clients.consumer.ConsumerRecord` — Represents a single record consumed from Kafka.
- `org.apache.kafka.clients.consumer.ConsumerRecords` — A collection of records returned by a Kafka consumer poll operation.
- `org.apache.kafka.clients.consumer.KafkaConsumer` — The client for consuming records from Kafka.
- `org.apache.kafka.clients.producer.Producer` — The client for producing records to Kafka.
- `java.sql.Connection` — Represents a connection to a database.
- `java.sql.PreparedStatement` — An object that represents a precompiled SQL statement.
- `java.sql.ResultSet` — An object that represents a database result set.
- `java.time.Duration` — Used to specify the timeout for Kafka consumer poll operations.
- `java.util.ArrayList` — Used for dynamic arrays, specifically for collecting `OrderProducts`.
- `java.util.List` — Interface for ordered collections.
- `java.util.concurrent.ExecutorService` — Manages a pool of threads for executing tasks concurrently.
- `java.util.concurrent.Executors` — Factory and utility methods for `ExecutorService`.
- `java.util.concurrent.TimeUnit` — Represents time durations at a given unit of granularity.
- `java.util.concurrent.atomic.AtomicBoolean` — A boolean value that may be updated atomically, used for thread-safe shutdown signaling.

#### Metadata
- File: `src/main/java/nb/edu/kafkaes/connect/ConnectESDataGenerator.java`
- Type: class
- Modifiers: public
- Annotations: None
- Extends: None
- Implements: None

#### Fields
- `producer`: `Producer<String, String>` — Kafka producer instance used to send enriched data to the Elasticsearch Kafka topic.
- `mapper`: `ObjectMapper` — Jackson `ObjectMapper` instance for serializing `ESRecord` objects to JSON.
- `shutdown`: `AtomicBoolean` — A flag to signal the consumer threads to initiate a graceful shutdown.
- `service`: `ExecutorService` — A fixed-size thread pool (3 threads) used to run multiple `getActiveOrderConsumer` tasks concurrently.
- `gson`: `Gson` — Google Gson instance for deserializing Kafka message values (JSON strings) into `OrderValue` objects.

#### Constructors
- `ConnectESDataGenerator()` — Default constructor. Initializes the `ObjectMapper`, `AtomicBoolean` shutdown flag, `ExecutorService` with 3 threads, and `Gson` instance.

#### Methods
- `init(): void`
  - **Summary**: Initializes the Kafka producer and starts multiple concurrent consumer threads to process active orders from Kafka.
  - **Business Logic**:
    1. Obtains a Kafka producer instance using `DemoUtilities.getProducer()`.
    2. Submits five instances of the `getActiveOrderConsumer` `Runnable` to the `ExecutorService`. Each `Runnable` will run in a separate thread, allowing for concurrent consumption and processing of Kafka messages.
  - **External calls**: `DemoUtilities.getProducer()`, `ExecutorService.submit()`.
  - **DB interactions**: None directly.
  - **Concurrency / Security / Caching**: Manages the creation and submission of concurrent tasks to an `ExecutorService`.
  - **Javadoc summary (if available)**: Not available.
  - **Testable behavior**:
    - Verify that the `producer` field is initialized.
    - Verify that five `Runnable` tasks are submitted to the `ExecutorService`.

- `getActiveOrderConsumer(final String id): Runnable`
  - **Summary**: Returns a `Runnable` task that continuously consumes messages from a Kafka topic, enriches the data by querying a relational database, and then produces the enriched data to another Kafka topic for Elasticsearch.
  - **Business Logic**:
    1. Creates a `KafkaConsumer` instance for the "connect-active-orders" topic, using a consumer group ID that can be overridden by a system property (`active-orders-group`).
    2. Enters a `while` loop that continues as long as the `shutdown` flag is `false`.
    3. Inside the loop, it polls the Kafka topic for records with a timeout of 1 second.
    4. If records are received:
       - Obtains a database connection from `DemoDataSource`.
       - Iterates through each `ConsumerRecord`:
         - Deserializes the Kafka message value (JSON string) into an `OrderValue` object using `gson`.
         - Retrieves `OrderRecord`, `CustomerRecord`, and a list of `OrderProducts` from the database using `getOrderRecord`, `getCustomerRecord`, and `getOrderProducts` methods, respectively.
         - Constructs an `ESRecord` object by combining the retrieved order, customer, and product data.
         - Serializes the `ESRecord` into a JSON string using `mapper.writeValueAsString()`.
         - Sends the JSON string to the "connect-active-orders-es" Kafka topic using `DemoUtilities.sendToTopic()`.
       - Commits the consumer offsets synchronously after processing all records in the batch.
    5. Includes error handling for message parsing and general exceptions during polling, with a short sleep before retrying.
    6. Closes the `KafkaConsumer` when the `shutdown` flag becomes `true`.
  - **External calls**: `DemoUtilities.getConsumer()`, `KafkaConsumer.poll()`, `DemoDataSource.getConnection()`, `Gson.fromJson()`, `getOrderRecord()`, `getCustomerRecord()`, `getOrderProducts()`, `ObjectMapper.writeValueAsString()`, `DemoUtilities.sendToTopic()`, `KafkaConsumer.commitSync()`, `KafkaConsumer.close()`.
  - **DB interactions**: Calls `DemoDataSource.getConnection()` to get a database connection, and then uses `getOrderRecord()`, `getCustomerRecord()`, `getOrderProducts()` to query the database.
  - **Concurrency / Security / Caching**: Designed to run concurrently in a separate thread. Includes exception handling and a graceful shutdown mechanism.
  - **Javadoc summary (if available)**: Not available.
  - **Testable behavior**:
    - Verify that the consumer correctly polls messages from the input topic.
    - Verify correct deserialization of Kafka message values into `OrderValue`.
    - Verify that database queries are executed correctly to enrich data.
    - Verify correct construction of `ESRecord` objects.
    - Verify correct serialization of `ESRecord` to JSON.
    - Verify that enriched messages are sent to the output Kafka topic.
    - Verify that consumer offsets are committed.
    - Verify the graceful shutdown behavior of the consumer loop.

- `getOrderRecord(Connection conn, String orderId): OrderRecord`
  - **Summary**: Retrieves an `OrderRecord` from the `demo.orders` table in the database based on the provided `orderId`.
  - **Business Logic**:
    1. Constructs a SQL `SELECT` statement to fetch order details (`id`, `cust_id`, `total`, `ts_placed`, `description`) from the `demo.orders` table where the `id` matches the input `orderId`.
    2. Creates a `PreparedStatement` and sets the `orderId` parameter.
    3. Executes the query and processes the `ResultSet` to populate an `OrderRecord` object.
  - **External calls**: Database interaction (JDBC).
  - **DB interactions**: Executes a `SELECT` query against the `demo.orders` table.
  - **Concurrency / Security / Caching**: None directly.
  - **Javadoc summary (if available)**: Not available.
  - **Testable behavior**:
    - Verify the generated SQL query is correct.
    - Verify that an `OrderRecord` is correctly instantiated and populated from the `ResultSet`.

- `getCustomerRecord(Connection conn, String customerId): CustomerRecord`
  - **Summary**: Retrieves a `CustomerRecord` from the `demo.customers` table in the database based on the provided `customerId`.
  - **Business Logic**:
    1. Constructs a SQL `SELECT` statement to fetch customer details (`id`, `address`, `region`, `name`) from the `demo.customers` table where the `id` matches the input `customerId`.
    2. Creates a `PreparedStatement` and sets the `customerId` parameter.
    3. Executes the query and processes the `ResultSet` to populate a `CustomerRecord` object.
  - **External calls**: Database interaction (JDBC).
  - **DB interactions**: Executes a `SELECT` query against the `demo.customers` table.
  - **Concurrency / Security / Caching**: None directly.
  - **Javadoc summary (if available)**: `@VisibleForTesting`
  - **Testable behavior**:
    - Verify the generated SQL query is correct.
    - Verify that a `CustomerRecord` is correctly instantiated and populated from the `ResultSet`.

- `getOrderProducts(Connection conn, String orderId): List<OrderProducts>`
  - **Summary**: Retrieves a list of `OrderProducts` associated with a given `orderId` from the `demo.orders_products` table in the database.
  - **Business Logic**:
    1. Constructs a SQL `SELECT` statement to fetch product details (`id`, `product_id`) from the `demo.orders_products` table where the `order_id` matches the input `orderId`.
    2. Creates a `PreparedStatement` and sets the `orderId` parameter.
    3. Executes the query and processes the `ResultSet` to populate a list of `OrderProducts` objects.
  - **External calls**: Database interaction (JDBC).
  - **DB interactions**: Executes a `SELECT` query against the `demo.orders_products` table.
  - **Concurrency / Security / Caching**: None directly.
  - **Javadoc summary (if available)**: `@VisibleForTesting`
  - **Testable behavior**:
    - Verify the generated SQL query is correct.
    - Verify that a `List<OrderProducts>` is correctly instantiated and populated from the `ResultSet`.

- `shutdown(): void`
  - **Summary**: Initiates a graceful shutdown of the `ConnectESDataGenerator`, stopping all consumer threads and closing Kafka producer resources.
  - **Business Logic**:
    1. Sets the `shutdown` `AtomicBoolean` flag to `true`, which signals all running `getActiveOrderConsumer` threads to terminate their processing loops.
    2. Initiates a graceful shutdown of the `ExecutorService`, preventing new tasks from being submitted and allowing existing tasks to complete.
    3. Waits for the `ExecutorService` to terminate for up to 15 seconds, allowing active tasks to finish.
    4. Flushes any buffered records in the Kafka producer and then closes the producer, releasing its resources.
  - **External calls**: `ExecutorService.shutdown()`, `ExecutorService.awaitTermination()`, `Producer.flush()`, `Producer.close()`.
  - **DB interactions**: None directly.
  - **Concurrency / Security / Caching**: Manages the graceful shutdown of a thread pool and Kafka producer.
  - **Javadoc summary (if available)**: Not available.
  - **Testable behavior**:
    - Verify that the `shutdown` flag is set to `true`.
    - Verify that `ExecutorService.shutdown()` is called.
    - Verify that `ExecutorService.awaitTermination()` is called with the specified timeout.
    - Verify that `producer.flush()` and `producer.close()` are invoked.

#### Relationships
- Associations: None directly.
- DI/service dependencies:
  - Depends on `DemoDataSource` for database connections.
  - Depends on `DemoUtilities` for Kafka client setup and message sending.
  - Uses `ObjectMapper` for JSON serialization and `Gson` for JSON deserialization.
  - Manages `Producer` and `KafkaConsumer` instances.

#### Database Layer
- Entity mappings: Data from `demo.orders`, `demo.customers`, and `demo.orders_products` tables are mapped into `OrderRecord`, `CustomerRecord`, and `OrderProducts` value objects, respectively.
- Repositories: Implicitly, the `getOrderRecord`, `getCustomerRecord`, and `getOrderProducts` methods act as data access methods.
- Queries: Executes `SELECT` queries to retrieve order, customer, and product information.

#### External Interactions
- HTTP endpoints called: None directly.
- Messaging topics/queues:
  - Consumes from Kafka topic: "connect-active-orders"
  - Produces to Kafka topic: "connect-active-orders-es"
- Filesystem/caches: None directly.

#### Dependencies
- `com.fasterxml.jackson.databind:jackson-databind`
- `com.google.code.gson:gson`
- `com.google.guava:guava` (for `@VisibleForTesting`)
- `org.apache.kafka:kafka-clients`
- `nb.edu.kafkaes.util.DemoDataSource`
- `nb.edu.kafkaes.util.DemoUtilities`
- `nb.edu.kafkaes.vo.CustomerRecord`
- `nb.edu.kafkaes.vo.ESRecord`
- `nb.edu.kafkaes.vo.OrderProducts`
- `nb.edu.kafkaes.vo.OrderRecord`
- `nb.edu.kafkaes.connect.OrderValue`

#### Links
- Related classes:
  - [DemoDataSource](specs/src/main/java/nb/edu/kafkaes/util/DemoDataSource.md)
  - [DemoUtilities](specs/src/main/java/nb/edu/kafkaes/util/DemoUtilities.md)
  - [CustomerRecord](specs/src/main/java/nb/edu/kafkaes/vo/CustomerRecord.md)
  - [ESRecord](specs/src/main/java/nb/edu/kafkaes/vo/ESRecord.md)
  - [OrderProducts](specs/src/main/java/nb/edu/kafkaes/vo/OrderProducts.md)
  - [OrderRecord](specs/src/main/java/nb/edu/kafkaes/vo/OrderRecord.md)
  - [OrderValue](specs/src/main/java/nb/edu/kafkaes/connect/OrderValue.md)

#### Notes
None.
