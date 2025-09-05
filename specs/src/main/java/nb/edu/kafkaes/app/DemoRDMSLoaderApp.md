### Class: `DemoRDMSLoaderApp`
**Package:** `nb.edu.kafkaes.app`

#### File Summary
This class serves as the entry point for a demonstration application focused on loading data from a Relational Database Management System (RDBMS).

#### Class Summary
`DemoRDMSLoaderApp` initializes and runs an `OrderDataLoader` in a separate thread to facilitate continuous or background loading of order data. It also ensures a graceful shutdown of the data loader when the application terminates.

#### Imports (Relevant)
- `nb.edu.kafkaes.sql.OrderDataLoader` — Used to initialize and manage the process of loading order data from an RDBMS.

#### Metadata
- File: `src/main/java/nb/edu/kafkaes/app/DemoRDMSLoaderApp.java`
- Type: class
- Modifiers: public
- Annotations: None
- Extends: None
- Implements: None

#### Fields
- `dataLoader`: `OrderDataLoader` — An instance responsible for loading order data from an RDBMS.

#### Constructors
- `DemoRDMSLoaderApp()` — Default constructor. Initializes the `DemoRDMSLoaderApp` instance.

#### Methods
- `main(String[] args): void`
  - **Summary**: The application's entry point. It starts the `OrderDataLoader` in a new thread and registers a shutdown hook for graceful termination.
  - **Business Logic**:
    1. Creates an instance of `OrderDataLoader`.
    2. Starts the `dataLoader` in a new `Thread`, indicating that data loading will occur in the background or continuously.
    3. Registers a runtime shutdown hook that ensures `dataLoader.shutdown()` is called when the JVM is terminating, allowing for clean resource release.
  - **External calls**: Orchestrates calls to `OrderDataLoader`, which is expected to interact with an RDBMS.
  - **DB interactions**: Directly orchestrates `OrderDataLoader`, which is designed for RDBMS data loading.
  - **Concurrency / Security / Caching**: Utilizes a new `Thread` for `OrderDataLoader` execution and a `Runtime.getRuntime().addShutdownHook` for concurrent, graceful application termination.
  - **Javadoc summary (if available)**: Not available.
  - **Testable behavior**:
    - Verify that an `OrderDataLoader` instance is created and started in a new thread.
    - Verify that `dataLoader.shutdown()` is invoked when the application receives a shutdown signal.

#### Relationships
- Associations: None directly.
- DI/service dependencies: Directly instantiates and depends on `OrderDataLoader`.

#### Database Layer
This class directly interacts with an RDBMS through the `OrderDataLoader` component to load order data.

#### External Interactions
This class orchestrates components that interact with an RDBMS for data loading.

#### Dependencies
- `nb.edu.kafkaes.sql.OrderDataLoader`

#### Links
- Related classes:
  - [OrderDataLoader](specs/src/main/java/nb/edu/kafkaes/sql/OrderDataLoader.md)

#### Notes
None.
