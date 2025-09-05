# Project: `kafka-es`

## Project Overview
The `kafka-es` project is a demonstration application designed to showcase data flow from relational databases (PostgreSQL and SQLite) through Apache Kafka (leveraging Kafka Connect) to Elasticsearch. It encompasses various components for data generation, Kafka message processing, and Elasticsearch indexing, illustrating a complete data pipeline.

## Build & Module Structure
This project is a single-module Gradle-based application.

### Gradle Configuration (`build.gradle`, `settings.gradle`)
- **Root Project Name**: `kafka-es`
- **Plugins**: `java`
- **Repositories**: `mavenCentral()`
- **Dependencies**:
  - `org.postgresql:postgresql:42.2.14` (PostgreSQL JDBC Driver)
  - `org.elasticsearch.client:elasticsearch-rest-high-level-client:7.8.0` (Elasticsearch High-Level REST Client)
  - `org.elasticsearch:elasticsearch:7.8.0` (Elasticsearch Core)
  - `com.google.code.gson:gson:2.8.6` (Google Gson for JSON processing)
  - `com.zaxxer:HikariCP:3.4.5` (HikariCP Connection Pool)
  - `com.google.guava:guava:28.2-jre` (Google Guava utilities)
  - `com.fasterxml.jackson.core:jackson-databind:2.9.8` (Jackson Databind for JSON processing)
  - `org.apache.kafka:kafka-clients:2.2.1` (Kafka Clients for producer/consumer)
  - `junit:junit:4.12` (JUnit for testing - `testCompile`)

## High-Level Architecture & Flows

### Component Overview
The architecture consists of several interconnected components:
- **Data Generators**: Java applications (`OrderDataLoader`, `ConnectOrderDataLoader`) that create and persist order data into a PostgreSQL database and/or publish directly to Kafka.
- **Kafka Connect**: Utilized for streaming data:
    - **JDBC Source Connector**: Streams data from PostgreSQL to Kafka topics.
    - **Elasticsearch Sink Connector**: Streams data from Kafka topics to Elasticsearch.
- **Kafka Consumers/Producers**: Custom Java applications (`DemoKafkaESApp`, `DemoRDMSLoaderApp`, `ConnectDemoApp`, `ESDataGenerator`, `ESDataLoader`) that directly interact with Kafka topics for various processing tasks.
- **Elasticsearch Data Loaders**: Java components (`ESDataLoader`, `ESDataGenerator`, `ConnectESDataGenerator`) that consume processed data from Kafka and index it into Elasticsearch.
- **Databases**: PostgreSQL (managed by `DemoDataSource`) serves as the primary relational data source, while SQLite is used in some Kafka Connect JDBC examples.

### Architecture Flow Diagram (Textual)
```mermaid
graph TD
    subgraph RDBMS
        DB_PG[PostgreSQL]
        DB_SQLITE[SQLite]
    end

    subgraph Kafka Ecosystem
        KAFKA_BROKER[Kafka Broker]
        KC_SOURCE[Kafka Connect JDBC Source]
        KC_SINK[Kafka Connect ES Sink]
        K_TOPIC_ORDERS[Kafka Topic: active-orders]
        K_TOPIC_ES[Kafka Topic: active-orders-es]
        K_TOPIC_CONNECT_ORDERS[Kafka Topic: connect-active-orders]
        K_TOPIC_CONNECT_ES[Kafka Topic: connect-active-orders-es]
        K_TOPIC_TEST_ES_SINK[Kafka Topic: test-elasticsearch-sink]
        K_TOPIC_ORDERS_RAW[Kafka Topic: orders (raw)]
        K_TOPIC_TEST_SQLITE_JDBC[Kafka Topic: test-sqlite-jdbc-*]
    end

    subgraph Java Applications
        APP_DEMO_KAFKA_ES[DemoKafkaESApp]
        APP_DEMO_RDBMS_LOADER[DemoRDMSLoaderApp]
        APP_CONNECT_DEMO[ConnectDemoApp]
        COMP_ES_DATA_LOADER[ESDataLoader (es package)]
        COMP_ES_DATA_GENERATOR_SQL[ESDataGenerator (sql package)]
        COMP_ORDER_DATA_LOADER_SQL[OrderDataLoader (sql package)]
        COMP_CONNECT_ES_DATA_GENERATOR[ConnectESDataGenerator (connect package)]
        COMP_CONNECT_ORDER_DATA_LOADER[ConnectOrderDataLoader (connect package)]
    end

    ELASTICSEARCH[Elasticsearch]

    COMP_ORDER_DATA_LOADER_SQL --> DB_PG
    COMP_ORDER_DATA_LOADER_SQL --> K_TOPIC_ORDERS

    COMP_CONNECT_ORDER_DATA_LOADER --> DB_PG
    COMP_CONNECT_ORDER_DATA_LOADER --> K_TOPIC_CONNECT_ORDERS

    KC_SOURCE -- (connect/source-rdbms/connect.properties) --> DB_PG
    KC_SOURCE --> K_TOPIC_CONNECT_ORDERS

    COMP_ES_DATA_GENERATOR_SQL -- Consumes from --> K_TOPIC_ORDERS
    COMP_ES_DATA_GENERATOR_SQL -- Enriches from --> DB_PG
    COMP_ES_DATA_GENERATOR_SQL -- Produces to --> K_TOPIC_ES

    COMP_CONNECT_ES_DATA_GENERATOR -- Consumes from --> K_TOPIC_CONNECT_ORDERS
    COMP_CONNECT_ES_DATA_GENERATOR -- Enriches from --> DB_PG
    COMP_CONNECT_ES_DATA_GENERATOR -- Produces to --> K_TOPIC_CONNECT_ES

    COMP_ES_DATA_LOADER -- Consumes from --> K_TOPIC_ES
    COMP_ES_DATA_LOADER --> ELASTICSEARCH

    KC_SINK -- (connect/sink-es/connect.properties) --> K_TOPIC_CONNECT_ES
    KC_SINK --> ELASTICSEARCH

    KC_SINK_SQLITE[Kafka Connect JDBC Sink (SQLite)] -- (connect/plugin/.../sink-quickstart-sqlite.properties) --> K_TOPIC_ORDERS_RAW
    KC_SINK_SQLITE --> DB_SQLITE

    KC_SOURCE_SQLITE[Kafka Connect JDBC Source (SQLite)] -- (connect/plugin/.../source-quickstart-sqlite.properties) --> DB_SQLITE
    KC_SOURCE_SQLITE --> K_TOPIC_TEST_SQLITE_JDBC

    APP_DEMO_KAFKA_ES --> COMP_ES_DATA_GENERATOR_SQL
    APP_DEMO_KAFKA_ES --> COMP_ES_DATA_LOADER

    APP_DEMO_RDBMS_LOADER --> COMP_ORDER_DATA_LOADER_SQL

    APP_CONNECT_DEMO --> COMP_CONNECT_ORDER_DATA_LOADER
    APP_CONNECT_DEMO --> COMP_CONNECT_ES_DATA_GENERATOR
```

### Sequence Narratives for Representative Request Flows

1.  **RDBMS to Kafka to Elasticsearch (via Kafka Connect)**:
    -   An `OrderDataLoader` or `ConnectOrderDataLoader` component generates new order data and persists it into the PostgreSQL database.
    -   The Kafka Connect JDBC Source Connector (`demo-source-jdbc`) continuously monitors the `orders` table in PostgreSQL for new or updated records (using the `ts_placed` timestamp column).
    -   Upon detecting changes, the connector streams these records to a Kafka topic (e.g., `connect-active-orders`).
    -   The Kafka Connect Elasticsearch Sink Connector (`demo-es-sink`) consumes messages from a Kafka topic (e.g., `connect-active-orders-es`, which might be populated by another processing step or directly by the source connector if transformations are applied).
    -   These messages, expected to be Elasticsearch-ready JSON documents, are then indexed or updated as documents in the Elasticsearch cluster.

2.  **Direct Java Application Flow (Data Generation & Elasticsearch Loading)**:
    -   The `DemoKafkaESApp` serves as the main entry point, initializing and managing the lifecycle of two key components: `ESDataGenerator` (from the `sql` package) and `ESDataLoader` (from the `es` package).
    -   The `ESDataGenerator` component starts multiple Kafka consumer threads. These threads consume raw order messages from a Kafka topic (e.g., "active-orders").
    -   For each consumed message, `ESDataGenerator` enriches the order data by querying the PostgreSQL database for associated customer and product information.
    -   The enriched data is then transformed into an `ESRecord` (Elasticsearch-ready format) and published to another Kafka topic (e.g., "active-orders-es").
    -   Concurrently, the `ESDataLoader` component starts multiple Kafka consumer threads. These threads consume the enriched `ESRecord` messages from the "active-orders-es" Kafka topic.
    -   Each `ESRecord` message is then indexed or updated as a document in the Elasticsearch cluster.

3.  **Direct Java Application Flow (RDBMS Data Loading and Kafka Publishing)**:
    -   The `DemoRDMSLoaderApp` initializes and runs an `OrderDataLoader` (from the `sql` package) in a separate thread.
    -   The `OrderDataLoader` component periodically generates synthetic order data, including customer and product details.
    -   This generated data is persisted into the PostgreSQL database.
    -   After successfully committing the data to the database, the `OrderDataLoader` publishes a simplified `OrderRecord` message (containing `orderId` and an "INSERT" action) to a Kafka topic (e.g., "active-orders"), signaling the creation of a new order event.

## REST API Specs (Root Only)
No explicit REST controllers (e.g., annotated with `@RestController`, JAX-RS, or Micronaut annotations) were identified within the Java source files. Therefore, no OpenAPI specification will be generated for this project.

## Configurations & Environment

### Runtime Environment
-   **Kafka Broker**: `localhost:9092` or `127.0.0.1:9092`
    -   Used by `DemoUtilities.java` for Kafka client setup.
    -   Used by Kafka Connect worker configurations (`connect/sink-es/worker.properties`, `connect/source-rdbms/worker.properties`).
-   **Elasticsearch Host**: `localhost:9200` or `127.0.0.1:9200`
    -   Used by `DemoUtilities.java` for Elasticsearch client setup.
    -   Used by Kafka Connect Elasticsearch Sink Connector configurations (`connect/plugin/.../quickstart-elasticsearch.properties`, `connect/sink-es/connect.properties`).
-   **PostgreSQL Database**: `jdbc:postgresql://localhost:5432/postgres`
    -   Used by `DemoDataSource.java` for connection pooling.
    -   Used by Kafka Connect JDBC Source Connector (`connect/source-rdbms/connect.properties`).
-   **SQLite Database**: `jdbc:sqlite:test.db`
    -   Used by Kafka Connect JDBC Sink/Source Connector quick-start examples (`connect/plugin/.../sink-quickstart-sqlite.properties`, `connect/plugin/.../source-quickstart-sqlite.properties`).
-   **Kafka Consumer Group IDs**:
    -   `es-group` (for `ESDataLoader`)
    -   `order-group` (default for `ESDataGenerator`)
    -   `connect-order-group` (default for `ConnectESDataGenerator`)
-   **Kafka Topics**:
    -   `active-orders` (produced by `OrderDataLoader`, consumed by `ESDataGenerator`)
    -   `active-orders-es` (produced by `ESDataGenerator`, consumed by `ESDataLoader`)
    -   `connect-active-orders` (produced by `ConnectOrderDataLoader`, consumed by `ConnectESDataGenerator`, also target for `demo-source-jdbc` Kafka Connect)
    -   `connect-active-orders-es` (produced by `ConnectESDataGenerator`, consumed by `demo-es-sink` Kafka Connect)
    -   `test-elasticsearch-sink` (consumed by `elasticsearch-sink` Kafka Connect)
    -   `orders` (consumed by `test-sink` Kafka Connect)
    -   `test-sqlite-jdbc-` (prefix for topics produced by `test-source-sqlite-jdbc-autoincrement` Kafka Connect)
-   **Kafka Connect Worker REST API Ports**:
    -   `10001` (for `connect/source-rdbms/worker.properties`)
    -   `10002` (for `connect/sink-es/worker.properties`)
-   **Database Timezone**: `Asia/Calcutta` (configured in `connect/source-rdbms/connect.properties`).

### Configuration Files Summary
-   [quickstart-elasticsearch.md](specs/config/quickstart-elasticsearch.md)
-   [sink-quickstart-sqlite.md](specs/config/sink-quickstart-sqlite.md)
-   [source-quickstart-sqlite.md](specs/config/source-quickstart-sqlite.md)
-   [connect-sink-es.md](specs/config/connect-sink-es.md)
-   [connect-sink-es-worker.md](specs/config/connect-sink-es-worker.md)
-   [connect-source-rdbms.md](specs/config/connect-source-rdbms.md)
-   [connect-source-rdbms-worker.md](specs/config/connect-source-rdbms-worker.md)

### Secrets Summary
-   **PostgreSQL Database Password**: `<REDACTED: postgres>` (hardcoded in `DemoDataSource.java` and `connect/source-rdbms/connect.properties`). This is a sensitive value and should be externalized and secured in a production environment.

## Infra & CI/CD
-   **`docker-compose.yml`**: This file likely defines the multi-container Docker environment for the project, including services for Kafka, Zookeeper, Elasticsearch, and potentially a PostgreSQL database. It orchestrates the setup of the entire data pipeline infrastructure.
-   **`db.scripts`**: This directory or file likely contains SQL scripts necessary for setting up the database schema (e.g., `demo.orders`, `demo.customers`, `demo.orders_products` tables) and potentially populating initial data.
-   **`gradlew`, `gradlew.bat`**: These are the Gradle Wrapper scripts, enabling consistent project builds across different environments without requiring a global Gradle installation.

## Other Files
The following files were skipped during specification generation as per the rules (dotfiles, images, binaries, keystores, etc.):
-   `.gitignore`
-   `approach1.jpg`
-   `approach2.jpg`
-   `README.md`
-   `build/` (directory)
-   `gradle/` (directory)
-   `specs/` (directory)
-   `connect/plugin/confluentinc-kafka-connect-elasticsearch-5.5.1/manifest.json`
-   `connect/plugin/confluentinc-kafka-connect-elasticsearch-5.5.1/assets/confluent.png`
-   `connect/plugin/confluentinc-kafka-connect-elasticsearch-5.5.1/assets/elasticsearch.jpg`
-   `connect/plugin/confluentinc-kafka-connect-elasticsearch-5.5.1/doc/LICENSE`
-   `connect/plugin/confluentinc-kafka-connect-elasticsearch-5.5.1/doc/licenses.html`
-   `connect/plugin/confluentinc-kafka-connect-elasticsearch-5.5.1/doc/README.md`
-   `connect/plugin/confluentinc-kafka-connect-elasticsearch-5.5.1/doc/version.txt`
-   `connect/plugin/confluentinc-kafka-connect-elasticsearch-5.5.1/doc/licenses/LICENSE-commons-codec-1.9.txt`
-   `connect/plugin/confluentinc-kafka-connect-elasticsearch-5.5.1/doc/licenses/LICENSE-commons-lang3-3.4.txt`
-   `connect/plugin/confluentinc-kafka-connect-elasticsearch-5.5.1/doc/licenses/LICENSE-commons-logging-1.2.txt`
-   `connect/plugin/confluentinc-kafka-connect-elasticsearch-5.5.1/doc/licenses/LICENSE-guava-18.0.txt`
-   `connect/plugin/confluentinc-kafka-connect-elasticsearch-5.5.1/doc/licenses/LICENSE-httpasyncclient-4.1.1.txt`
-   `connect/plugin/confluentinc-kafka-connect-elasticsearch-5.5.1/doc/licenses/LICENSE-httpclient-4.5.1.txt`
-   `connect/plugin/confluentinc-kafka-connect-elasticsearch-5.5.1/doc/licenses/LICENSE-httpcore-4.4.4.txt`
-   `connect/plugin/confluentinc-kafka-connect-elasticsearch-5.5.1/doc/licenses/LICENSE-httpcore-nio-4.4.4.txt`
-   `connect/plugin/confluentinc-kafka-connect-elasticsearch-5.5.1/doc/licenses/LICENSE-jest-2.0.0.txt`
-   `connect/plugin/confluentinc-kafka-connect-elasticsearch-5.5.1/doc/licenses/LICENSE-jest-common-2.0.0.txt`
-   `connect/plugin/confluentinc-kafka-connect-elasticsearch-5.5.1/doc/notices/NOTICE-httpasyncclient-4.1.1.txt`
-   `connect/plugin/confluentinc-kafka-connect-elasticsearch-5.5.1/doc/notices/NOTICE-httpclient-4.5.1.txt`
-   `connect/plugin/confluentinc-kafka-connect-elasticsearch-5.5.1/doc/notices/NOTICE-httpcore-4.4.4.txt`
-   `connect/plugin/confluentinc-kafka-connect-elasticsearch-5.5.1/doc/notices/NOTICE-httpcore-nio-4.4.4.txt`
-   `connect/plugin/confluentinc-kafka-connect-elasticsearch-5.5.1/lib/common-utils-5.5.1.jar`
-   `connect/plugin/confluentinc-kafka-connect-elasticsearch-5.5.1/lib/commons-codec-1.9.jar`
-   `connect/plugin/confluentinc-kafka-connect-elasticsearch-5.5.1/lib/commons-logging-1.2.jar`
-   `connect/plugin/confluentinc-kafka-connect-elasticsearch-5.5.1/lib/gson-2.8.5.jar`
-   `connect/plugin/confluentinc-kafka-connect-elasticsearch-5.5.1/lib/guava-25.0-jre.jar`
-   `connect/plugin/confluentinc-kafka-connect-elasticsearch-5.5.1/lib/httpasyncclient-4.1.3.jar`
-   `connect/plugin/confluentinc-kafka-connect-elasticsearch-5.5.1/lib/httpclient-4.5.3.jar`
-   `connect/plugin/confluentinc-kafka-connect-elasticsearch-5.5.1/lib/httpcore-4.4.6.jar`
-   `connect/plugin/confluentinc-kafka-connect-elasticsearch-5.5.1/lib/httpcore-nio-4.4.6.jar`
-   `connect/plugin/confluentinc-kafka-connect-elasticsearch-5.5.1/lib/jest-6.3.1.jar`
-   `connect/plugin/confluentinc-kafka-connect-elasticsearch-5.5.1/lib/jest-common-6.3.1.jar`
-   `connect/plugin/confluentinc-kafka-connect-elasticsearch-5.5.1/lib/kafka-connect-elasticsearch-5.5.1.jar`
-   `connect/plugin/confluentinc-kafka-connect-elasticsearch-5.5.1/lib/slf4j-api-1.7.26.jar`
-   `connect/plugin/confluentinc-kafka-connect-jdbc-5.5.1/manifest.json`
-   `connect/plugin/confluentinc-kafka-connect-jdbc-5.5.1/assets/confluent.png`
-   `connect/plugin/confluentinc-kafka-connect-jdbc-5.5.1/assets/jdbc.jpg`
-   `connect/plugin/confluentinc-kafka-connect-jdbc-5.5.1/doc/LICENSE`
-   `connect/plugin/confluentinc-kafka-connect-jdbc-5.5.1/doc/licenses.html`
-   `connect/plugin/confluentinc-kafka-connect-jdbc-5.5.1/doc/NOTICE`
-   `connect/plugin/confluentinc-kafka-connect-jdbc-5.5.1/doc/README.md`
-   `connect/plugin/confluentinc-kafka-connect-jdbc-5.5.1/doc/version.txt`
-   `connect/plugin/confluentinc-kafka-connect-jdbc-5.5.1/doc/licenses/LICENSE-kafka-connect-jdbc-5.5.0-SNAPSHOT.txt`
-   `connect/plugin/confluentinc-kafka-connect-jdbc-5.5.1/doc/licenses/LICENSE-postgresql-42.2.10.txt`
-   `connect/plugin/confluentinc-kafka-connect-jdbc-5.5.1/doc/licenses/LICENSE-sqlite-jdbc-3.25.2.txt`
-   `connect/plugin/confluentinc-kafka-connect-jdbc-5.5.1/etc/sink-quickstart-sqlite.properties` (summarized in `specs/config`)
-   `connect/plugin/confluentinc-kafka-connect-jdbc-5.5.1/etc/source-quickstart-sqlite.properties` (summarized in `specs/config`)
-   `connect/plugin/confluentinc-kafka-connect-jdbc-5.5.1/lib/common-utils-5.5.1.jar`
-   `connect/plugin/confluentinc-kafka-connect-jdbc-5.5.1/lib/jtds-1.3.1.jar`
-   `connect/plugin/confluentinc-kafka-connect-jdbc-5.5.1/lib/kafka-connect-jdbc-5.5.1.jar`
-   `connect/plugin/confluentinc-kafka-connect-jdbc-5.5.1/lib/postgresql-42.2.10.jar`
-   `connect/plugin/confluentinc-kafka-connect-jdbc-5.5.1/lib/slf4j-api-1.7.26.jar`
-   `connect/plugin/confluentinc-kafka-connect-jdbc-5.5.1/lib/sqlite-jdbc-3.25.2.jar`
-   `gradle/wrapper/gradle-wrapper.jar`
-   `gradle/wrapper/gradle-wrapper.properties`

## Statistics Snapshot
(Will be generated in `specs/summary/statistics.md`)
