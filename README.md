# Backend Intern Assignment – Machine Events Backend

## 1. Architecture

The application follows a layered Spring Boot architecture:

Controller → Service → Repository → Database

- **Controller Layer**
    - Exposes REST APIs (`/events/batch`, `/stats`, `/stats/top-defect-lines`)
    - Handles request/response mapping only
    - No business logic

- **Service Layer**
    - Core business logic
    - Validation, deduplication, update rules
    - Statistics calculation
    - Runs inside transactional boundaries

- **Repository Layer**
    - Spring Data JPA repositories
    - Responsible for database access

- **Database Layer**
    - Relational database (MySQL)
    - Enforces data integrity using primary key and indexes

This separation keeps the system maintainable, testable, and easy to reason about.

---

## 2. Deduplication & Update Logic

Each event is uniquely identified by `eventId`.

### Payload Comparison
To detect identical vs different payloads:
- A **SHA-256 payload hash** is computed from:
    - eventId
    - eventTime
    - machineId
    - durationMs
    - defectCount
- Hash comparison is used instead of deep object comparison for performance.

### Deduplication Rules
1. **Same eventId + identical payload**
    - Event is treated as duplicate
    - Ignored (deduped)

2. **Same eventId + different payload**
    - Compare `receivedTime`
    - If incoming record has newer `receivedTime` → update
    - If older → ignore

The “winning” record is always the one with the **newer receivedTime**.

---

## 3. Thread Safety

Thread safety is guaranteed using **database-backed consistency**, not in-memory locks.

Key mechanisms:
- `eventId` is the **PRIMARY KEY**
- Database enforces uniqueness under concurrent inserts
- Ingestion logic runs inside a **transaction**
- Atomic read–modify–write semantics provided by the database

No explicit Java locks are used.

Concurrency tests intentionally attempt duplicate inserts to prove correctness.
Duplicate key errors are expected and demonstrate database-level safety.

---

## 4. Data Model

### Event Table Schema

| Column Name     | Description |
|-----------------|-------------|
| event_id        | Primary key (deduplication key) |
| event_time      | Used for query windows |
| received_time   | Set by backend, used for updates |
| machine_id      | Machine identifier |
| duration_ms     | Event duration |
| defect_count    | Defect count (-1 means unknown) |
| payload_hash    | SHA-256 hash of payload |

### Indexes
- `(machine_id, event_time)` for fast stats queries
- `(event_time)` for time-range scans

---

## 5. Performance Strategy (1000 events < 1 sec)

To meet the performance requirement:
- Batch ingestion handled in memory
- Minimal database calls
- Single transaction per batch
- Hash-based payload comparison
- Indexed database queries
- Avoided unnecessary object creation

Measured performance comfortably meets the requirement.

---

## 6. Edge Cases & Assumptions

### Validation Rules
- `durationMs < 0` or `> 6 hours` → rejected
- `eventTime > now + 15 minutes` → rejected
- `receivedTime` from client is ignored

### Special Rules
- `defectCount = -1`
    - Event is stored
    - Ignored in defect calculations

### Assumptions
- `eventId` is globally unique
- MySQL is used for both development and tests
- MachineId is treated as lineId for defect-line stats

Tradeoff:
- Deduplication logic is in service layer (clear & portable)
  instead of DB-specific UPSERTs.

---

## 7. Setup & Run Instructions

### Prerequisites
- Java 17
- Maven
- MySQL running locally

### Database Setup
Create the database in MySQL:

```mysql
CREATE DATABASE assignment;
```

### Configuration
Update the `application.properties` file with your local MySQL credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/assignment  
spring.datasource.username=your_username  
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update  
spring.jpa.open-in-view=false
```

### Run Application
Start the Spring Boot application using Maven:

```bash
mvn spring-boot:run
```
The application will start on port 8080.

### Run Tests
Run all unit and integration tests:

```bash
mvn test
```



