### Class: `ESRecord`
**Package:** `nb.edu.kafkaes.vo`

#### File Summary
This class is a Plain Old Java Object (POJO) that aggregates `OrderRecord`, `CustomerRecord`, and a list of `OrderProducts` into a single, comprehensive record. It is specifically designed to represent an enriched order document for Elasticsearch.

#### Class Summary
`ESRecord` serves as a composite value object or data transfer object (DTO) specifically designed to represent a fully enriched order document suitable for indexing into Elasticsearch. It combines related data from various sources (order details, customer information, and product lists) into a single, coherent structure, facilitating efficient storage and retrieval in a NoSQL database like Elasticsearch.

#### Imports (Relevant)
- `com.fasterxml.jackson.annotation.JsonInclude` — Annotation used to specify inclusion criteria for properties during JSON serialization, ensuring that fields with null values are not included in the output.
- `java.util.List` — Standard Java interface for ordered collections, used here to hold multiple `OrderProducts` associated with an order.

#### Metadata
- File: `src/main/java/nb/edu/kafkaes/vo/ESRecord.java`
- Type: class
- Modifiers: public
- Annotations: `@JsonInclude(JsonInclude.Include.NON_NULL)`
- Extends: None
- Implements: None

#### Fields
- `order`: `OrderRecord` — An instance of `OrderRecord` containing the primary details of the order.
- `customer`: `CustomerRecord` — An instance of `CustomerRecord` containing the details of the customer who placed the order.
- `products`: `List<OrderProducts>` — A list of `OrderProducts` instances, detailing the products included in the order.

#### Constructors
- `ESRecord()` — Default constructor. Initializes a new, empty `ESRecord` instance.
- `ESRecord(OrderRecord order, CustomerRecord customer, List<OrderProducts> products)` — Parameterized constructor. Initializes an `ESRecord` with the provided `OrderRecord`, `CustomerRecord`, and list of `OrderProducts`.

#### Methods
- `getOrder(): OrderRecord`
  - **Summary**: Returns the `OrderRecord` associated with this Elasticsearch record.
  - **Business Logic**: Standard getter method for the `order` field.
  - **External calls**: None.
  - **DB interactions**: None.
  - **Concurrency / Security / Caching**: None.
  - **Javadoc summary (if available)**: Not available.
  - **Testable behavior**:
    - Verify that the returned `OrderRecord` instance matches the one set during construction or via reflection.

- `getCustomer(): CustomerRecord`
  - **Summary**: Returns the `CustomerRecord` associated with this Elasticsearch record.
  - **Business Logic**: Standard getter method for the `customer` field.
  - **External calls**: None.
  - **DB interactions**: None.
  - **Concurrency / Security / Caching**: None.
  - **Javadoc summary (if available)**: Not available.
  - **Testable behavior**:
    - Verify that the returned `CustomerRecord` instance matches the one set during construction or via reflection.

- `getProducts(): List<OrderProducts>`
  - **Summary**: Returns the list of `OrderProducts` associated with this Elasticsearch record.
  - **Business Logic**: Standard getter method for the `products` field.
  - **External calls**: None.
  - **DB interactions**: None.
  - **Concurrency / Security / Caching**: None.
  - **Javadoc summary (if available)**: Not available.
  - **Testable behavior**:
    - Verify that the returned `List<OrderProducts>` instance matches the one set during construction or via reflection.

#### Relationships
- Associations: This class composes (`has-a` relationship) instances of `OrderRecord`, `CustomerRecord`, and a `List` of `OrderProducts`.

#### Database Layer
None directly. This class is a data structure that aggregates data typically sourced from a relational database (via `OrderRecord`, `CustomerRecord`, `OrderProducts`) before being sent to Elasticsearch.

#### External Interactions
Instances of `ESRecord` are serialized to JSON format and then sent to Kafka topics, which are subsequently consumed by Elasticsearch data loaders for indexing into an Elasticsearch cluster.

#### Dependencies
- `com.fasterxml.jackson.core:jackson-annotations` (for `@JsonInclude`)
- `nb.edu.kafkaes.vo.OrderRecord`
- `nb.edu.kafkaes.vo.CustomerRecord`
- `nb.edu.kafkaes.vo.OrderProducts`

#### Links
- Related classes:
  - [OrderRecord](specs/src/main/java/nb/edu/kafkaes/vo/OrderRecord.md) (composed within `ESRecord`)
  - [CustomerRecord](specs/src/main/java/nb/edu/kafkaes/vo/CustomerRecord.md) (composed within `ESRecord`)
  - [OrderProducts](specs/src/main/java/nb/edu/kafkaes/vo/OrderProducts.md) (composed within `ESRecord`)
  - [ESDataGenerator](specs/src/main/java/nb/edu/kafkaes/sql/ESDataGenerator.md) (creates `ESRecord` instances)
  - [ConnectESDataGenerator](specs/src/main/java/nb/edu/kafkaes/connect/ConnectESDataGenerator.md) (creates `ESRecord` instances)

#### Notes
- The `@JsonInclude(JsonInclude.Include.NON_NULL)` annotation is applied at the class level, ensuring that if any of the composed fields (`order`, `customer`, `products`) are `null`, they will be omitted when the `ESRecord` object is serialized to JSON. This helps in creating cleaner and potentially smaller JSON documents for Elasticsearch.
