### Class: `DemoUtilities`
**Package:** `nb.edu.kafkaes.util`

#### File Summary
This class provides static utility methods for configuring and obtaining Kafka producers, Kafka consumers, and an Elasticsearch `RestHighLevelClient`, along with a helper method for sending messages to Kafka topics.

#### Class Summary
`DemoUtilities` centralizes the creation and configuration of external client instances (Kafka and Elasticsearch). It simplifies the setup of these clients for demonstration purposes, ensuring consistent configuration across the application and abstracting away the boilerplate code for client initialization.

#### Imports (Relevant)
- `org.apache.http.HttpHost` — Represents a host (hostname, port, scheme) for HTTP connections, used in configuring the Elasticsearch `RestClient`.
- `org.apache.kafka.clients.consumer.ConsumerConfig` — Configuration properties for Kafka consumers.
- `org.apache.kafka.clients.consumer.KafkaConsumer` — The client for consuming records from Kafka.
- `org.apache.kafka.clients.producer.Callback` — An interface for a callback function that is executed when a record has been acknowledged by the Kafka server.
- `org.apache.kafka.clients.producer.KafkaProducer` — The client for producing records to Kafka.
- `org.apache.kafka.clients.producer.Producer` — The interface for a Kafka producer.
- `org.apache.kafka.clients.producer.ProducerConfig` — Configuration properties for Kafka producers.
- `org.apache.kafka.clients.producer.ProducerRecord` — A key/value pair to be sent to Kafka.
- `org.apache.kafka.clients.producer.RecordMetadata` — The metadata for a record that has been acknowledged by the server.
- `org.elasticsearch.client.RestClient` — The low-level REST client for Elasticsearch.
- `org.elasticsearch.client.RestHighLevelClient` — The high-level REST client for interacting with an Elasticsearch cluster.
- `java.util.Arrays` — Utility class for array-related operations, used here for `consumer.subscribe()`.
- `java.util.Properties` — A persistent set of properties, used for Kafka client configurations.
- `java.util.concurrent.Future` — Represents the result of an asynchronous computation, used for Kafka send operations.

#### Metadata
- File: `src/main/java/nb/edu/kafkaes/util/DemoUtilities.java`
- Type: class
- Modifiers: public
- Annotations: None
- Extends: None
- Implements: None

#### Fields
None. All members are static methods.

#### Constructors
- `DemoUtilities()` — Implicit private constructor (as it's a utility class with only static methods).

#### Methods
- `getProducer(): Producer<String, String>`
  - **Summary**: Creates and returns a configured Kafka `Producer` instance for sending string key-value messages.
  - **Business Logic**:
    1. Initializes a `Properties` object with standard Kafka producer configurations.
    2. Sets `BOOTSTRAP_SERVERS_CONFIG` to "localhost:9092".
    3. Configures reconnect backoff, `ACKS_CONFIG` to "all" for high durability, `RETRIES_CONFIG` to 5, and `LINGER_MS_CONFIG` to 1ms for batching.
    4. Specifies `StringSerializer` for both key and value serialization.
    5. Instantiates and returns a `KafkaProducer` with these properties.
  - **External calls**: Instantiates `KafkaProducer`.
  - **DB interactions**: None.
  - **Concurrency / Security / Caching**: Configures producer for reliability and basic performance.
  - **Javadoc summary (if available)**: Not available.
  - **Testable behavior**:
    - Verify that a non-null `Producer` instance is returned.
    - Verify that the returned producer's configuration matches the specified properties (e.g., bootstrap servers, serializers).

- `getConsumer(String id, String groupId, String topic): KafkaConsumer<String, String>`
  - **Summary**: Creates and returns a configured Kafka `Consumer` instance, subscribed to a specified topic, for consuming string key-value messages.
  - **Business Logic**:
    1. Initializes a `Properties` object with standard Kafka consumer configurations.
    2. Sets `BOOTSTRAP_SERVERS_CONFIG` to "localhost:9092".
    3. Configures `CLIENT_ID_CONFIG` and `GROUP_ID_CONFIG` based on provided parameters.
    4. Sets `MAX_POLL_RECORDS_CONFIG` to 50, reconnect backoff, and `AUTO_OFFSET_RESET_CONFIG` to "earliest".
    5. Specifies `StringDeserializer` for both key and value deserialization.
    6. Explicitly disables auto-commit (`ENABLE_AUTO_COMMIT_CONFIG` to "false") for manual offset management.
    7. Instantiates a `KafkaConsumer` and subscribes it to the given `topic`.
    8. Returns the configured `KafkaConsumer`.
  - **External calls**: Instantiates `KafkaConsumer`, `consumer.subscribe()`.
  - **DB interactions**: None.
  - **Concurrency / Security / Caching**: Configures consumer for a specific group, client ID, and manual offset management.
  - **Javadoc summary (if available)**: Not available.
  - **Testable behavior**:
    - Verify that a non-null `KafkaConsumer` instance is returned.
    - Verify that the consumer is subscribed to the correct topic.
    - Verify that essential configuration properties are set correctly (e.g., group ID, auto-offset reset, auto-commit disabled).

- `getEsClient(): RestHighLevelClient`
  - **Summary**: Creates and returns an Elasticsearch `RestHighLevelClient` instance configured to connect to a local Elasticsearch instance.
  - **Business Logic**:
    1. Constructs an `HttpHost` object for "localhost" on port 9200 using the "http" scheme.
    2. Builds a low-level `RestClient` using this `HttpHost`.
    3. Instantiates and returns a `RestHighLevelClient` based on the low-level client.
  - **External calls**: Instantiates `RestHighLevelClient`.
  - **DB interactions**: None.
  - **Concurrency / Security / Caching**: None directly.
  - **Javadoc summary (if available)**: Not available.
  - **Testable behavior**:
    - Verify that a non-null `RestHighLevelClient` instance is returned.
    - Verify that the client is configured to connect to "localhost:9200" via HTTP.

- `sendToTopic(Producer<String, String> producer, final String topic, String key, String value, boolean sync) throws Exception`
  - **Summary**: Sends a key-value message to a specified Kafka topic, with an optional synchronous wait for acknowledgment from the Kafka broker.
  - **Business Logic**:
    1. Defines an anonymous `Callback` implementation to log the result of the send operation (success with partition/offset or failure with exception).
    2. Creates a `ProducerRecord` with the provided `topic`, `key`, and `value`.
    3. Sends the `ProducerRecord` using the given `producer` instance, attaching the `callback`.
    4. If the `sync` parameter is `true`, it calls `meta.get()` on the returned `Future` to block until the message is acknowledged, making the operation synchronous.
    5. Flushes the producer to ensure any buffered records are sent immediately.
  - **External calls**: `Producer.send()`, `Future.get()` (if sync), `Producer.flush()`.
  - **DB interactions**: None.
  - **Concurrency / Security / Caching**: Supports both asynchronous (default) and synchronous message sending to Kafka. Includes logging for send results.
  - **Javadoc summary (if available)**: Not available.
  - **Testable behavior**:
    - Verify that `producer.send()` is called with the correct `ProducerRecord` and `Callback`.
    - If `sync` is true, verify that `Future.get()` is invoked.
    - Verify that `producer.flush()` is always called.
    - Verify the `Callback` logic correctly logs success or failure.

#### Relationships
- Associations: None directly.
- DI/service dependencies: This class provides client instances and utility methods that are dependencies for various Kafka and Elasticsearch interacting components (e.g., `ESDataGenerator`, `OrderDataLoader`, `ConnectESDataGenerator`, `ConnectOrderDataLoader`, `ESDataLoader`, `DemoKafkaESApp`, `DemoRDMSLoaderApp`, `ConnectDemoApp`).

#### Database Layer
None directly. This class focuses on Kafka and Elasticsearch client utilities.

#### External Interactions
- HTTP endpoints called: Interacts with Elasticsearch via its REST API (localhost:9200).
- Messaging topics/queues: Interacts with Kafka brokers (localhost:9092) for producing and consuming messages.
- Filesystem/caches: None directly.

#### Dependencies
- `org.apache.kafka:kafka-clients` — For Kafka producer and consumer functionality.
- `org.elasticsearch.client:elasticsearch-rest-high-level-client` — For Elasticsearch client functionality.
- `org.elasticsearch:elasticsearch` — Core Elasticsearch dependencies (implied by `RestHighLevelClient`).
- `org.apache.httpcomponents:httpclient` — Apache HTTP Client for low-level HTTP communication (implied by `HttpHost`).

#### Links
- Related classes:
  - [ESDataGenerator](specs/src/main/java/nb/edu/kafkaes/sql/ESDataGenerator.md)
  - [OrderDataLoader](specs/src/main/java/nb/edu/kafkaes/sql/OrderDataLoader.md)
  - [ConnectESDataGenerator](specs/src/main/java/nb/edu/kafkaes/connect/ConnectESDataGenerator.md)
  - [ConnectOrderDataLoader](specs/src/main/java/nb.edu/kafkaes/connect/ConnectOrderDataLoader.md)
  - [ESDataLoader](specs/src/main/java/nb/edu/kafkaes/es/ESDataLoader.md)
  - [DemoKafkaESApp](specs/src/main/java/nb/edu/kafkaes/app/DemoKafkaESApp.md)
  - [DemoRDMSLoaderApp](specs/src/main/java/nb/edu/kafkaes/app/DemoRDMSLoaderApp.md)
  - [ConnectDemoApp](specs/src/main/java/nb/edu/kafkaes/connect/ConnectDemoApp.md)

#### Notes
- **Hardcoded Connection Details**: Kafka broker (`localhost:9092`) and Elasticsearch host (`localhost:9200`) details are hardcoded. These should be externalized (e.g., via environment variables or configuration files) for production environments.
- **Manual Offset Commit**: Kafka consumers are configured for manual offset commits, requiring explicit `consumer.commitSync()` calls.
