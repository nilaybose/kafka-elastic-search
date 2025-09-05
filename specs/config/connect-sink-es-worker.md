### Configuration: `connect/sink-es/worker.properties`

#### File Summary
This file configures a standalone Kafka Connect worker for the Elasticsearch sink. It defines the Kafka broker connection, key/value converters, REST API settings for the worker, and offset management.

#### Configuration Details
- **`bootstrap.servers`**: `127.0.0.1:9092`
  - The Kafka broker(s) to connect to.
- **`key.converter`**: `org.apache.kafka.connect.storage.StringConverter`
  - The converter class for Kafka message keys. `StringConverter` treats keys as plain strings.
- **`key.converter.schemas.enable`**: `false`
  - Disables schema handling for keys, treating them as raw strings.
- **`value.converter`**: `org.apache.kafka.connect.json.JsonConverter`
  - The converter class for Kafka message values. `JsonConverter` handles JSON formatted values.
- **`value.converter.schemas.enable`**: `false`
  - Disables schema handling for values, treating them as raw JSON.
- **`rest.port`**: `10002`
  - The port on which the Kafka Connect worker's REST API will listen.
- **`rest.host.name`**: `127.0.0.1`
  - The hostname or IP address on which the Kafka Connect worker's REST API will bind.
- **`offset.flush.interval.ms`**: `10000`
  - The interval in milliseconds at which to flush offsets to storage.
- **`plugin.path`**: `../plugin`
  - The path to the directory containing Kafka Connect plugins (connectors).
- **`offset.storage.file.filename`**: `./standalone.offsets`
  - The file path where offsets will be stored for this standalone worker.

#### Environment & Secrets
- **Kafka Broker**: `127.0.0.1:9092` (hardcoded)
  - The Kafka broker address.
- **Connect REST API Port**: `10002` (hardcoded)
  - The port for the Connect worker's REST interface.

#### Notes
- This configuration is specifically for a standalone Kafka Connect worker.
- It uses `StringConverter` for keys and `JsonConverter` for values, indicating that messages are expected to be simple strings and JSON objects, respectively.
- Hardcoded Kafka broker and REST API host/port details should be externalized in production.
