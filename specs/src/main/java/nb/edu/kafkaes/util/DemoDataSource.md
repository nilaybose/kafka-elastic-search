### Class: `DemoDataSource`
**Package:** `nb.edu.kafkaes.util`

#### File Summary
This class provides a static utility for obtaining database connections using HikariCP, configured for a PostgreSQL database. It ensures efficient and managed access to the database for various application components.

#### Class Summary
`DemoDataSource` acts as a singleton data source provider, encapsulating the configuration and management of a HikariCP connection pool. Its primary role is to offer a centralized and efficient way for the application to acquire database connections, promoting resource reuse and performance.

#### Imports (Relevant)
- `com.zaxxer.hikari.HikariConfig` — Used to configure the properties of the HikariCP connection pool.
- `com.zaxxer.hikari.HikariDataSource` — The HikariCP implementation of a JDBC `DataSource`, providing connection pooling capabilities.
- `java.sql.Connection` — The standard JDBC interface representing a connection to a database.
- `java.sql.SQLException` — The exception class that provides information on a database access error or other errors.

#### Metadata
- File: `src/main/java/nb/edu/kafkaes/util/DemoDataSource.java`
- Type: class
- Modifiers: public
- Annotations: None
- Extends: None
- Implements: None

#### Fields
- `config`: `HikariConfig` — A static instance of `HikariConfig` used to define the connection pool's properties, such as JDBC URL, credentials, and pool size.
- `ds`: `HikariDataSource` — A static instance of `HikariDataSource`, representing the initialized HikariCP connection pool. It is initialized once in a static block.

#### Constructors
- `private DemoDataSource()` — A private constructor prevents direct instantiation of this utility class, ensuring that only the static `ds` instance is used.

#### Methods
- `getConnection(): Connection`
  - **Summary**: Returns an active database connection from the HikariCP connection pool.
  - **Business Logic**: This method delegates the call to the `ds.getConnection()` method, which retrieves an available connection from the managed pool. If no connection is available or an error occurs, an `SQLException` is thrown.
  - **External calls**: `ds.getConnection()`.
  - **DB interactions**: Provides database connections from a managed pool.
  - **Concurrency / Security / Caching**: Manages connection pooling, which inherently handles concurrent access to connections and optimizes connection reuse.
  - **Javadoc summary (if available)**: Not available.
  - **Testable behavior**:
    - Verify that calling `getConnection()` returns a valid `java.sql.Connection` object.
    - Verify that `SQLException` is thrown if the data source fails to provide a connection (e.g., due to misconfiguration or database unavailability).

#### Relationships
- Associations: None directly.
- DI/service dependencies: This class is a dependency for any component requiring a database connection (e.g., `ESDataGenerator`, `OrderDataLoader`, `ConnectESDataGenerator`, `ConnectOrderDataLoader`).

#### Database Layer
This class is a foundational component of the database layer, providing the central mechanism for acquiring and managing database connections for the application.

#### External Interactions
- HTTP endpoints called: None directly.
- Messaging topics/queues: None directly.
- Filesystem/caches: None directly.
- Database: Connects to a PostgreSQL database using the JDBC URL `jdbc:postgresql://localhost:5432/postgres`.

#### Dependencies
- `com.zaxxer:HikariCP` — The high-performance JDBC connection pool.
- `org.postgresql:postgresql` — The PostgreSQL JDBC driver (implied by the `jdbcUrl`).

#### Links
- Related classes:
  - [ESDataGenerator](specs/src/main/java/nb/edu/kafkaes/sql/ESDataGenerator.md)
  - [OrderDataLoader](specs/src/main/java/nb/edu/kafkaes/sql/OrderDataLoader.md)
  - [ConnectESDataGenerator](specs/src/main/java/nb/edu/kafkaes/connect/ConnectESDataGenerator.md)
  - [ConnectOrderDataLoader](specs/src/main/java/nb.edu/kafkaes/connect/ConnectOrderDataLoader.md)

#### Notes
- **Hardcoded Credentials**: The database URL (`jdbc:postgresql://localhost:5432/postgres`), username (`postgres`), and password (`postgres`) are hardcoded within the static initializer. In a production environment, these sensitive details should be externalized (e.g., via environment variables or a configuration management system) and redacted in specifications.
- **Auto-Commit**: `config.setAutoCommit(false)` is explicitly set, meaning transactions must be manually committed or rolled back by the application code.
