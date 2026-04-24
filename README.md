# Bank Account Application

A small core banking solution built with Java 17, Spring Boot, MyBatis, PostgreSQL, and RabbitMQ. It supports account management, multi-currency balances, and transaction processing, with all state changes published to RabbitMQ.

---

## Table of Contents

- [Technologies](#technologies)
- [Getting Started](#getting-started)
- [Architecture & Design Decisions](#architecture--design-decisions)
- [Performance Estimate](#performance-estimate)
- [Horizontal Scaling Considerations](#horizontal-scaling-considerations)
- [AI Usage](#ai-usage)

---

## Technologies

- **Java 21**
- **Spring Boot**
- **MyBatis** — SQL mapping
- **Gradle** — build tool
- **PostgreSQL** — primary database
- **RabbitMQ** — message broker
- **JUnit 5** — integration testing
- **Docker & Docker Compose** — containerization

---

## Getting Started

### Prerequisites

- Java 21+
- Docker & Docker Compose
- Gradle (or use the included `./gradlew` wrapper)

### 1. Build the application

```bash
./gradlew clean build
```

This compiles the code and runs all tests. The resulting JAR will be placed in `build/libs/`.

### 2. Start all services

```bash
docker-compose up --build
```

This will start:
- The Spring Boot application
- PostgreSQL (with the database schema initialised automatically via SQL scripts)
- RabbitMQ

The application will be available at `http://localhost:8080`.

### 3. Stop all services

```bash
docker-compose down
```

To also remove volumes (wipe the database):

```bash
docker-compose down -v
```

### Running Tests

```bash
./gradlew test
```

Test coverage is at least 80%, verified with integration tests against a real PostgreSQL instance.

---

## Architecture & Design Decisions

### Database schema

Accounts, balances, and transactions are stored in three separate tables. Balances are updated atomically within the same database transaction as the transaction record insert, ensuring consistency.

### Concurrency & balance updates

Balance updates for `OUT` transactions use a `SELECT ... FOR UPDATE` lock on the balance row before checking and decrementing. This prevents race conditions when concurrent requests attempt to spend from the same balance simultaneously.

### RabbitMQ publishing

Every account creation, balance update, and transaction is published to RabbitMQ after a successful database commit.

### Plain SQL migrations via Docker

Database schema is initialised through SQL init scripts mounted into the PostgreSQL Docker container. This keeps the setup simple and avoids adding a migration library dependency for this assignment.

---

## Performance Estimate

On a typical development machine, the application handles approximately **350 transactions per second** under load.

The main bottleneck is the database write + row-level lock on the balance update, which is expected. RabbitMQ publishing adds minimal overhead as it happens asynchronously after the DB commit.

The number was measured using k6.

---

## Horizontal Scaling Considerations

To scale the application horizontally (multiple instances behind a load balancer), the following must be addressed:

**Database concurrency** — the `SELECT ... FOR UPDATE` locking strategy already works correctly across multiple application instances, since the lock lives in PostgreSQL. No application-level change is needed here.

**RabbitMQ connections** — each instance maintains its own connection pool to RabbitMQ. This is fine; RabbitMQ handles multiple producers naturally.

**Statelessness** — the application is stateless (no in-memory session or cache), so any instance can handle any request. A load balancer (e.g. NGINX or an AWS ALB) can distribute traffic freely.

**Database connection pool sizing** — with multiple instances, the total number of connections to PostgreSQL grows. It is important to size the pool (via `spring.datasource.hikari.maximum-pool-size`) conservatively per instance and consider using a connection pooler like [PgBouncer](https://www.pgbouncer.org/) in front of PostgreSQL.

**Idempotency** — if a client retries a request after a timeout, a duplicate transaction could be created. Adding a client-supplied idempotency key (stored in the DB with a unique constraint) would prevent duplicates under retry conditions.

---

## AI Usage

AI was used in this project to assist with:

**Project Structure** - Guidance on setting up a Spring Boot project with MyBatis and PostgreSQL.

**SQL & Mapping** - Assistance with SQL scripts and fixing MyBatis XML/Annotation mapping issues

**Testing** - Help with setting up MockMvc integration tests and improving test coverage.

**Documentation** - Grammar correction and professional formatting of the README file.
