### Configuration: `connect/sink-es/connect.properties`

#### File Summary
This file configures a Kafka Connect Elasticsearch Sink Connector named `demo-es-sink`. It specifies the Kafka topic to consume from, the Elasticsearch connection details, and various behaviors for indexing, error handling, and schema management.

#### Configuration Details
- **`name`**: `demo-es-sink`
  - The logical name for this connector instance.
- **`connector.class`**: `io.confluent.connect.elasticsearch.ElasticsearchSinkConnector`
  - Specifies the fully qualified class name of the Kafka Connect Elasticsearch Sink Connector.
- **`topics`**: `connect-active-orders-es`
  - The Kafka topic from which the connector will consume messages. This topic is expected to contain Elasticsearch-ready JSON documents.
- **`tasks.max`**: `1`
  - The maximum number of tasks that can be created for this connector.
- **`connection.url`**: `http://127.0.0.1:9200`
  - The URL for connecting to the Elasticsearch cluster.
- **`type.name`**: `_doc`
  - The Elasticsearch document type name to be used for indexed documents. (Note: `_doc` is the default type in Elasticsearch 7.x and later).
- **`key.ignore`**: `false`
  - Indicates that the Kafka message key should be used as the Elasticsearch document ID.
- **`write.method`**: `upsert`
  - Specifies the write method to Elasticsearch. `upsert` means it will update a document if it exists, or insert it if it does not.
- **`schema.ignore`**: `true`
  - If set to `true`, the connector will ignore the Kafka Connect schema and treat the message value as raw JSON.
- **`behavior.on.malformed.documents`**: `warn`
  - Defines the behavior when encountering malformed documents. `warn` will log a warning and continue processing.
- **`retry.backoff.ms`**: `1000`
  - The time in milliseconds to wait before retrying a failed operation.
- **`auto.create.indices.at.start`**: `true`
  - If set to `true`, the connector will attempt to create the target Elasticsearch index if it does not exist when the connector starts.
- **`drop.invalid.message`**: `true`
  - If set to `true`, invalid messages will be dropped instead of causing the connector to fail.

#### Environment & Secrets
- **Elasticsearch Host**: `127.0.0.1:9200` (hardcoded)
  - The target Elasticsearch instance.

#### Notes
- This configuration is for a Kafka Connect sink that pushes data from Kafka to Elasticsearch.
- It uses `upsert` as the write method, which is suitable for continuous data synchronization where records might be updated.
- `schema.ignore=true` is common when Kafka messages are already in a format directly consumable by Elasticsearch (e.g., raw JSON).
- Hardcoded Elasticsearch connection details should be externalized in production.
