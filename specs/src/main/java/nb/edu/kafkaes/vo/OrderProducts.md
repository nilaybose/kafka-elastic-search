### Class: `OrderProducts`
**Package:** `nb.edu.kafkaes.vo`

#### File Summary
This class is a Plain Old Java Object (POJO) representing a product entry within an order, including its own ID, the associated order ID, and the product ID. It is designed for data transfer and storage.

#### Class Summary
`OrderProducts` serves as a value object or data transfer object (DTO) to encapsulate the details of a single product item within an order. Its primary use is for transferring and storing product information, particularly when this data is retrieved from a database and combined with other records (like order data) for further processing or indexing into systems like Elasticsearch.

#### Imports (Relevant)
- `com.fasterxml.jackson.annotation.JsonInclude` — Annotation used to specify inclusion criteria for properties during JSON serialization, ensuring that fields with null values are not included in the output.

#### Metadata
- File: `src/main/java/nb/edu/kafkaes/vo/OrderProducts.java`
- Type: class
- Modifiers: public
- Annotations: `@JsonInclude(JsonInclude.Include.NON_NULL)`
- Extends: None
- Implements: None

#### Fields
- `id`: `String` — The unique identifier for this specific order product entry.
- `orderId`: `String` — The ID of the order to which this product entry belongs.
- `productId`: `String` — The identifier of the product itself.

#### Constructors
- `OrderProducts()` — Default constructor. Initializes a new, empty `OrderProducts` instance.
- `OrderProducts(String id, String orderId, String productId)` — Parameterized constructor. Initializes an `OrderProducts` with the provided ID, order ID, and product ID.

#### Methods
- `getId(): String`
  - **Summary**: Returns the unique identifier for this order product entry.
  - **Business Logic**: Standard getter method for the `id` field.
  - **External calls**: None.
  - **DB interactions**: None.
  - **Concurrency / Security / Caching**: None.
  - **Javadoc summary (if available)**: Not available.
  - **Testable behavior**:
    - Verify that the returned string matches the value previously set for `id`.

- `getOrderId(): String`
  - **Summary**: Returns the ID of the order to which this product belongs.
  - **Business Logic**: Standard getter method for the `orderId` field.
  - **External calls**: None.
  - **DB interactions**: None.
  - **Concurrency / Security / Caching**: None.
  - **Javadoc summary (if available)**: Not available.
  - **Testable behavior**:
    - Verify that the returned string matches the value previously set for `orderId`.

- `getProductId(): String`
  - **Summary**: Returns the identifier of the product itself.
  - **Business Logic**: Standard getter method for the `productId` field.
  - **External calls**: None.
  - **DB interactions**: None.
  - **Concurrency / Security / Caching**: None.
  - **Javadoc summary (if available)**: Not available.
  - **Testable behavior**:
    - Verify that the returned string matches the value previously set for `productId`.

#### Relationships
- Associations: None directly.
- DI/service dependencies: None directly.

#### Database Layer
This class is used to represent data mapped from the `demo.orders_products` table in the relational database.

#### External Interactions
None directly. Instances of this class are typically created from database query results and then embedded within other data structures (like `ESRecord`) that are sent to external systems.

#### Dependencies
- `com.fasterxml.jackson.core:jackson-annotations` (for `@JsonInclude`)

#### Links
- Related classes:
  - [ESDataGenerator](specs/src/main/java/nb/edu/kafkaes/sql/ESDataGenerator.md) (retrieves and uses `OrderProducts`)
  - [ConnectESDataGenerator](specs/src/main/java/nb/edu/kafkaes/connect/ConnectESDataGenerator.md) (retrieves and uses `OrderProducts`)
  - [ESRecord](specs/src/main/java/nb/edu/kafkaes/vo/ESRecord.md) (contains a list of `OrderProducts`)

#### Notes
- The `@JsonInclude(JsonInclude.Include.NON_NULL)` annotation is applied at the class level, ensuring that any fields within `OrderProducts` that have `null` values will be omitted when the object is serialized to JSON. This helps in creating cleaner and potentially smaller JSON payloads.
