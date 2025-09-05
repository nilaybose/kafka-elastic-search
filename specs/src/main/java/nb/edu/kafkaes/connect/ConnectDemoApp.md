### Class: `ConnectDemoApp`
**Package:** `nb.edu.kafkaes.connect`

#### File Summary
This class serves as a demonstration application for Kafka Connect, orchestrating data loading and generation processes related to Elasticsearch.

#### Class Summary
`ConnectDemoApp` initializes and runs `ConnectOrderDataLoader` in a separate thread for continuous data loading (likely from Kafka) and `ConnectESDataGenerator` for data generation. It ensures a graceful shutdown for the data loader.

#### Imports (Relevant)
- `nb.edu.kafkaes.connect.ConnectOrderDataLoader` — Used to initialize and manage the process of loading order data, likely via Kafka Connect.
- `nb.edu.kafkaes.connect.ConnectESDataGenerator` — Used to initialize and manage the process of generating data for Elasticsearch, likely via Kafka Connect.

#### Metadata
- File: `src/main/java/nb/edu/kafkaes/connect/ConnectDemoApp.java`
- Type: class
- Modifiers: public
- Annotations: None
- Extends: None
- Implements: None

#### Fields
- `dataLoader`: `ConnectOrderDataLoader` — An instance responsible for loading order data.
- `esDataGenerator`: `ConnectESDataGenerator` — An instance responsible for generating data for Elasticsearch.

#### Constructors
- `ConnectDemoApp()` — Default constructor. Initializes the `ConnectDemoApp` instance.

#### Methods
- `main(String[] args): void`
  - **Summary**: The application's entry point. It starts Kafka Connect related data loading and generation components and registers a shutdown hook for graceful termination of the data loader.
  - **Business Logic**:
    1. Creates an instance of `ConnectOrderDataLoader`.
    2. Starts the `dataLoader` in a new `Thread`, indicating that data loading will occur in the background or continuously, likely from Kafka.
    3. Creates an instance of `ConnectESDataGenerator`.
    4. Calls the `init()` method on `esDataGenerator` to start data generation.
    5. Registers a runtime shutdown hook that ensures `dataLoader.shutdown()` is called when the JVM is terminating, allowing for clean resource release for the data loader.
  - **External calls**: Orchestrates calls to `ConnectOrderDataLoader` and `ConnectESDataGenerator`, which are expected to interact with Kafka and Elasticsearch.
  - **DB interactions**: Orchestrates components that may interact with a database (e.g., `ConnectOrderDataLoader` might read from a database).
  - **Concurrency / Security / Caching**: Utilizes a new `Thread` for `ConnectOrderDataLoader` execution and a `Runtime.getRuntime().addShutdownHook` for concurrent, graceful application termination.
  - **Javadoc summary (if available)**: Not available.
  - **Testable behavior**:
    - Verify that a `ConnectOrderDataLoader` instance is created and started in a new thread.
    - Verify that a `ConnectESDataGenerator` instance is created and its `init()` method is invoked.
    - Verify that `dataLoader.shutdown()` is invoked when the application receives a shutdown signal.
    - Note: Consider if `esDataGenerator.shutdown()` should also be explicitly called in the shutdown hook for complete resource management.

#### Relationships
- Associations: None directly.
- DI/service dependencies: Directly instantiates and depends on `ConnectOrderDataLoader` and `ConnectESDataGenerator`.

#### Database Layer
This class orchestrates components that may interact with a database (e.g., `ConnectOrderDataLoader` might source data from a database).

#### External Interactions
This class orchestrates components that interact with Kafka for data streaming and Elasticsearch for data generation/loading.

#### Dependencies
- `nb.edu.kafkaes.connect.ConnectOrderDataLoader`
- `nb.edu.kafkaes.connect.ConnectESDataGenerator`

#### Links
- Related classes:
  - [ConnectOrderDataLoader](specs/src/main/java/nb/edu/kafkaes/connect/ConnectOrderDataLoader.md)
  - [ConnectESDataGenerator](specs/src/main/java/nb/edu/kafkaes/connect/ConnectESDataGenerator.md)

#### Notes
- The import `nb.edu.kafkaes.es.ESDataLoader` is present in the source file but not directly used within the `main` method of this class.
- The `shutdown()` method for `ConnectESDataGenerator` is not explicitly called in the registered shutdown hook, which might lead to unreleased resources for that component upon application termination.
