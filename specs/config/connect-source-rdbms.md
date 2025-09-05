### Configuration: `connect/source-rdbms/connect.properties`

#### File Summary
This file configures a Kafka Connect JDBC Source Connector named `demo-source-jdbc`. It defines the PostgreSQL database connection details, the table to monitor, and the strategy for detecting new or updated rows (timestamp mode). It also includes commented-out examples for data transformations.

#### Configuration Details
- **`name`**: `demo-source-jdbc`
  - The logical name for this connector instance.
- **`connector.class`**: `io.confluent.connect.jdbc.JdbcSourceConnector`
  - Specifies the fully qualified class name of the Kafka Connect JDBC Source Connector.
- **`tasks.max`**: `1`
  - The maximum number of tasks that can be created for this connector.
- **`topic.prefix`**: `connect-active-`
  - A prefix that will be added to the Kafka topic names. For example, the `orders` table will be written to the topic `connect-active-orders`.
- **`connection.url`**: `jdbc:postgresql://localhost:5432/postgres`
  - The JDBC URL for connecting to the PostgreSQL database.
- **`connection.user`**: `postgres`
  - The username for connecting to the PostgreSQL database.
- **`connection.password`**: `<REDACTED: postgres>`
  - The password for connecting to the PostgreSQL database. (Redacted for security)
- **`catalog.pattern`**: `demo`
  - A catalog name pattern to match against. This limits the tables scanned to those within the `demo` catalog/schema.
- **`mode`**: `timestamp`
  - Specifies the mode for detecting new or updated rows. `timestamp` mode uses a timestamp column.
- **`timestamp.column.name`**: `ts_placed`
  - The name of the timestamp column used to detect new or updated rows in the `orders` table.
- **`table.whitelist`**: `orders`
  - A comma-separated list of tables to include. Only the `orders` table will be monitored.
- **`validate.non.null`**: `false`
  - If set to `false`, the connector will not validate that the timestamp column is non-null.
- **`timestamp.initial`**: `0`
  - The initial timestamp value to use when starting the connector for the first time.
- **`db.timezone`**: `Asia/Calcutta`
  - The database timezone, used for handling timestamp values correctly.
- **`transforms`**: (commented out)
  - Placeholder for Kafka Connect Single Message Transformations (SMT) to modify records before they are written to Kafka. Examples for `ValueToKey`, `ExtractField`, `ReplaceField`, `RenameField`, `InsertField` are provided but commented out.

#### Environment & Secrets
- **PostgreSQL Database**: `localhost:5432/postgres` (hardcoded)
  - The target PostgreSQL instance.
- **Database User**: `postgres` (hardcoded)
- **Database Password**: `<REDACTED: postgres>` (hardcoded, redacted)
- **Database Timezone**: `Asia/Calcutta` (hardcoded)

#### Notes
- This configuration sets up a Kafka Connect source to stream data from a PostgreSQL database's `orders` table into Kafka topics.
- It uses `timestamp` mode with `ts_placed` column for change data capture.
- Hardcoded database connection details and credentials should be externalized and secured in production environments.
- The commented-out `transforms` section indicates potential for data manipulation before publishing to Kafka, such as creating a message key, dropping fields, renaming fields, or inserting static fields.
