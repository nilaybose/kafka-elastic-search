### Class: `CustomerRecord`
**Package:** `nb.edu.kafkaes.vo`

#### File Summary
This class is a Plain Old Java Object (POJO) representing a customer's record, including their ID, address, region, and name. It is designed for data transfer and storage, particularly when enriching order data.

#### Class Summary
`CustomerRecord` serves as a value object or data transfer object (DTO) to encapsulate customer-related data. Its primary use is for transferring and storing customer information, especially when this data is retrieved from a database and combined with other records (like order data) for further processing or indexing into systems like Elasticsearch.

#### Imports (Relevant)
- `com.fasterxml.jackson.annotation.JsonInclude` — Annotation used to specify inclusion criteria for properties during JSON serialization, ensuring that fields with null values are not included.

#### Metadata
- File: `src/main/java/nb/edu/kafkaes/vo/CustomerRecord.java`
- Type: class
- Modifiers: public
- Annotations: `@JsonInclude(JsonInclude.Include.NON_NULL)`
- Extends: None
- Implements: None

#### Fields
- `id`: `String` — The unique identifier for the customer.
- `address`: `String` — The physical address of the customer.
- `region`: `String` — The geographical region associated with the customer.
- `name`: `String` — The full name of the customer.

#### Constructors
- `CustomerRecord()` — Default constructor. Initializes a new, empty `CustomerRecord` instance.
- `CustomerRecord(String id, String address, String region, String name)` — Parameterized constructor. Initializes a `CustomerRecord` with the provided ID, address, region, and name.

#### Methods
- `getAddress(): String`
  - **Summary**: Returns the customer's address.
  - **Business Logic**: Standard getter method for the `address` field.
  - **External calls**: None.
  - **DB interactions**: None.
  - **Concurrency / Security / Caching**: None.
  - **Javadoc summary (if available)**: Not available.
  - **Testable behavior**:
    - Verify that the returned string matches the value previously set for `address`.

- `getId(): String`
  - **Summary**: Returns the unique identifier of the customer.
  - **Business Logic**: Standard getter method for the `id` field.
  - **External calls**: None.
  - **DB interactions**: None.
  - **Concurrency / Security / Caching**: None.
  - **Javadoc summary (if available)**: Not available.
  - **Testable behavior**:
    - Verify that the returned string matches the value previously set for `id`.

- `getRegion(): String`
  - **Summary**: Returns the customer's region.
  - **Business Logic**: Standard getter method for the `region` field.
  - **External calls**: None.
  - **DB interactions**: None.
  - **Concurrency / Security / Caching**: None.
  - **Javadoc summary (if available)**: Not available.
  - **Testable behavior**:
    - Verify that the returned string matches the value previously set for `region`.

- `getName(): String`
  - **Summary**: Returns the customer's name.
  - **Business Logic**: Standard getter method for the `name` field.
  - **External calls**: None.
  - **DB interactions**: None.
  - **Concurrency / Security / Caching**: None.
  - **Javadoc summary (if available)**: Not available.
  - **Testable behavior**:
    - Verify that the returned string matches the value previously set for `name`.

#### Relationships
- Associations: None directly.
- DI/service dependencies: None directly.

#### Database Layer
This class is used to represent data mapped from the `demo.customers` table in the relational database.

#### External Interactions
None directly. Instances of this class are typically created from database query results and then embedded within other data structures (like `ESRecord`) that are sent to external systems.

#### Dependencies
- `com.fasterxml.jackson.core:jackson-annotations` (for `@JsonInclude`)

#### Links
- Related classes:
  - [ESDataGenerator](specs/src/main/java/nb/edu/kafkaes/sql/ESDataGenerator.md) (retrieves and uses `CustomerRecord`)
  - [ConnectESDataGenerator](specs/src/main/java/nb/edu/kafkaes/connect/ConnectESDataGenerator.md) (retrieves and uses `CustomerRecord`)
  - [ESRecord](specs/src/main/java/nb/edu/kafkaes/vo/ESRecord.md) (contains a `CustomerRecord`)

#### Notes
- The `@JsonInclude(JsonInclude.Include.NON_NULL)` annotation is applied at the class level, ensuring that any fields within `CustomerRecord` that have `null` values will be omitted when the object is serialized to JSON. This helps in creating cleaner and potentially smaller JSON payloads.
