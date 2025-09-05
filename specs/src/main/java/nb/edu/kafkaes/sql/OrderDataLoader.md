### Class: `OrderDataLoader`
**Package:** `nb.edu.kafkaes.sql`

#### File Summary
This class is a `Runnable` that generates synthetic order data, persists it into a relational database, and then publishes a notification about the new order to a Kafka topic.

#### Class Summary
`OrderDataLoader` acts as a data generator and publisher. It creates new customer and order records, stores them in a database, and then uses a Kafka producer to signal the creation of a new order. It demonstrates the initial data creation, persistence, and event publishing step in a Kafka-Elasticsearch pipeline, intended to be run in a separate thread.

#### Imports (Relevant)
- `com.fasterxml.jackson.databind.ObjectMapper` — Used for serializing `OrderRecord` objects to JSON strings before sending them to Kafka.
- `com.google.common.annotations.VisibleForTesting` — Annotation used to mark methods primarily for testing purposes.
- `nb.edu.kafkaes.util.DemoDataSource` — Provides database connection pooling for efficient access to the relational database.
- `nb.edu.kafkaes.util.DemoUtilities` — Utility class for obtaining Kafka producer instances and sending messages to Kafka topics.
- `nb.edu.kafkaes.vo.OrderRecord` — Value object representing order data, used for both database interaction and Kafka messages.
- `org.apache.kafka.clients.producer.Producer` — The client for producing records to Kafka.
- `java.sql.Connection` — Represents a connection to a database.
- `java.sql.PreparedStatement` — An object that represents a precompiled SQL statement.
- `java.sql.Timestamp` — Represents a SQL `TIMESTAMP` value.
- `java.util.Random` — Used for generating random numbers, specifically for order totals.
- `java.util.UUID` — Used for generating universally unique identifiers for records.
- `java.util.concurrent.atomic.AtomicBoolean` — A boolean value that may be updated atomically, used for thread-safe shutdown signaling.

#### Metadata
- File: `src/main/java/nb/edu/kafkaes/sql/OrderDataLoader.java`
- Type: class
- Modifiers: public
- Annotations: None
- Extends: None
- Implements: `Runnable`

#### Fields
- `producer`: `Producer<String, String>` — Kafka producer instance used to send messages to Kafka topics.
- `mapper`: `ObjectMapper` — Jackson `ObjectMapper` instance for serializing objects to JSON.
- `shutdown`: `AtomicBoolean` — A flag to signal the `run` method to initiate a graceful shutdown.

#### Constructors
- `OrderDataLoader()` — Default constructor. Initializes the `ObjectMapper` and `AtomicBoolean` shutdown flag.

#### Methods
- `run(): void`
  - **Summary**: The main execution logic for this `Runnable`. It initializes a Kafka producer, creates a few sample orders, persists them to the database, and then calls `shutdown()`.
  - **Business Logic**:
    1. Obtains a Kafka producer instance using `DemoUtilities.getProducer()`.
    2. Enters a loop that iterates three times to simulate the creation of multiple orders.
    3. Inside the loop:
       - Calls `createOrder()` with a hardcoded customer ID ("a44d3eb4-24ec-42e3-bec6-454165592515") to generate and persist an order.
       - Prints the ID of the newly created order to the console.
       - Pauses the current thread for 1 second to simulate a delay between order creations.
       - Includes a `try-catch` block to handle exceptions during order creation, printing an error message and pausing for 3 seconds before continuing.
    4. After the loop completes, it calls the `shutdown()` method to close resources.
  - **External calls**: `DemoUtilities.getProducer()`, `createOrder()`, `shutdown()`.
  - **DB interactions**: Orchestrates calls to `createOrder()` which interacts with the database.
  - **Concurrency / Security / Caching**: Designed to run in a separate thread due to implementing `Runnable`. Includes basic error handling and delays.
  - **Javadoc summary (if available)**: Not available.
  - **Testable behavior**:
    - Verify that the `producer` field is initialized.
    - Verify that `createOrder()` is invoked three times.
    - Verify that `shutdown()` is called after the order creation loop.

- `loadCustomer(): void`
  - **Summary**: Inserts a new customer record into the `demo.customers` table with a randomly generated UUID and static address, region, and name.
  - **Business Logic**:
    1. Defines an SQL `INSERT` statement for the `demo.customers` table.
    2. Obtains a database connection from `DemoDataSource`.
    3. Creates a `PreparedStatement` and sets the parameters: a random `UUID` for the ID, "KA" for address, "india" for region, and "John" for name.
    4. Executes the `INSERT` statement.
    5. Commits the transaction to persist the new customer record.
  - **External calls**: Database interaction (JDBC).
  - **DB interactions**: Executes an `INSERT` query against the `demo.customers` table.
  - **Concurrency / Security / Caching**: None directly.
  - **Javadoc summary (if available)**: `@VisibleForTesting`
  - **Testable behavior**:
    - Verify the generated SQL `INSERT` statement is correct.
    - Verify that a new customer record is successfully inserted into the database.
    - Verify that the transaction is committed.

- `createOrder(String customer): String`
  - **Summary**: Creates a new order record and associated product records in the database, then publishes an `OrderRecord` to a Kafka topic, returning the generated order ID.
  - **Business Logic**:
    1. Defines SQL `INSERT` statements for the `demo.orders` and `demo.orders_products` tables.
    2. Generates a unique `orderId` using `UUID.randomUUID().toString()`.
    3. Obtains a database connection from `DemoDataSource`.
    4. Creates `PreparedStatement` instances for both order and order products insertions.
    5. Inside a `try` block:
       - Generates a random integer for the order `total`.
       - Sets parameters for the `orders` prepared statement: `orderId`, `customer` ID, `total`, current timestamp, and a description.
       - Executes the `orders` `INSERT` statement.
       - Adds three product entries ("tv", "cycle", "book") to the `orders_products` prepared statement as a batch, each with a new `UUID` and the generated `orderId`.
       - Executes the batch insert for products.
       - Commits the transaction to persist both the order and its associated products.
       - After successful database commit, creates an `OrderRecord` object with the generated `orderId` and an "INSERT" action.
       - Serializes this `OrderRecord` object into a JSON string using `mapper.writeValueAsString()`.
       - Sends the JSON string to the "active-orders" Kafka topic using `DemoUtilities.sendToTopic()`.
    6. Includes a `catch` block to handle exceptions during order creation, printing an error message and rolling back the transaction to maintain data consistency.
    7. Returns the generated `orderId`.
  - **External calls**: Database interaction (JDBC), Kafka producer (`DemoUtilities.sendToTopic()`).
  - **DB interactions**: Executes `INSERT` queries against the `demo.orders` and `demo.orders_products` tables. Manages database transactions (commit/rollback).
  - **Concurrency / Security / Caching**: Uses `Random` for data generation. Handles database transactions.
  - **Javadoc summary (if available)**: `@VisibleForTesting`
  - **Testable behavior**:
    - Verify the generated SQL `INSERT` statements are correct.
    - Verify that a unique `orderId` is generated.
    - Verify that an order record and three product records are successfully inserted.
    - Verify that the transaction is committed on success and rolled back on failure.
    - Verify that an `OrderRecord` is created and serialized to JSON.
    - Verify that the JSON message is sent to the "active-orders" Kafka topic.

- `shutdown(): void`
  - **Summary**: Initiates a graceful shutdown, setting the shutdown flag and closing the Kafka producer.
  - **Business Logic**:
    1. Sets the `shutdown` `AtomicBoolean` flag to `true`, which can be used by other parts of the application to stop ongoing processes.
    2. Flushes any buffered records in the Kafka producer to ensure all messages are sent.
    3. Closes the Kafka producer, releasing its resources.
  - **External calls**: `Producer.flush()`, `Producer.close()`.
  - **DB interactions**: None directly.
  - **Concurrency / Security / Caching**: Manages the graceful shutdown of the Kafka producer.
  - **Javadoc summary (if available)**: Not available.
  - **Testable behavior**:
    - Verify that the `shutdown` flag is set to `true`.
    - Verify that `producer.flush()` is called.
    - Verify that `producer.close()` is called.

#### Relationships
- Associations: None directly.
- DI/service dependencies:
  - Depends on `DemoDataSource` for database connections.
  - Depends on `DemoUtilities` for Kafka producer setup and message sending.
  - Uses `ObjectMapper` for JSON serialization.
  - Manages a `Producer` instance.

#### Database Layer
- Entity mappings: Inserts data into `demo.customers`, `demo.orders`, and `demo.orders_products` tables.
- Repositories: Implicitly, `loadCustomer()` and `createOrder()` act as data access methods for customer and order data.
- Queries: Executes `INSERT` queries to create new customer, order, and order product records.

#### External Interactions
- HTTP endpoints called: None directly.
- Messaging topics/queues: Uses a Kafka `Producer` to send messages to the "active-orders" topic.
- Filesystem/caches: None directly.

#### Dependencies
- `com.fasterxml.jackson.databind:jackson-databind`
- `com.google.guava:guava` (for `@VisibleForTesting`)
- `org.apache.kafka:kafka-clients`
- `nb.edu.kafkaes.util.DemoDataSource`
- `nb.edu.kafkaes.util.DemoUtilities`
- `nb.edu.kafkaes.vo.OrderRecord`

#### Links
- Related classes:
  - [DemoDataSource](specs/src/main/java/nb/edu/kafkaes/util/DemoDataSource.md)
  - [DemoUtilities](specs/src/main/java/nb/edu/kafkaes/util/DemoUtilities.md)
  - [OrderRecord](specs/src/main/java/nb/edu/kafkaes/vo/OrderRecord.md)

#### Notes
- The `run()` method currently only creates 3 orders and then shuts down, which might not be suitable for a continuous data loader as implied by its name and `Runnable` implementation.
