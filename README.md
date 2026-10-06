# WareFlow – Enterprise Warehouse Management System (WMS)

WareFlow is an enterprise-oriented Warehouse Management System built with Java and Spring Boot.

The project is designed to manage warehouse operations such as users, roles, inventory, storage locations, material movements, and warehouse workflows through a structured and scalable backend architecture.

> **Project Status:** 🚧 Active Development

---

## 📌 Overview

WareFlow aims to simulate a real-world enterprise Warehouse Management System while following modern backend development practices.

The project focuses on:

* Clean backend architecture
* Role-based access control
* Reliable database migrations
* Structured domain modeling
* RESTful API development
* Scalable warehouse workflows
* Enterprise-oriented software design

---

## 🛠 Tech Stack

### Backend

* Java
* Spring Boot
* Spring Web
* Spring Data JPA
* Hibernate
* Maven

### Database

* PostgreSQL
* Flyway Database Migrations

### Development

* IntelliJ IDEA
* Git
* GitHub
* Docker

---

## 🏗 Architecture

WareFlow follows a layered backend architecture:

```text
Client
   │
   ▼
REST Controller
   │
   ▼
Service Layer
   │
   ▼
Repository Layer
   │
   ▼
PostgreSQL Database
```

The application separates responsibilities between controllers, business logic, persistence, and domain entities.

---

## 📂 Project Structure

```text
src/main/java/com/wareflow
│
├── controller
├── service
├── repository
├── entity
├── dto
├── config
├── exception
└── WareflowApplication.java
```

The structure may evolve as the project grows.

---

## 🗄 Database

WareFlow uses PostgreSQL as its main relational database.

Database schema changes are managed through Flyway migrations.

Current database configuration includes:

* PostgreSQL 17
* Flyway migrations enabled
* Hibernate schema validation
* `ddl-auto=validate`
* `open-in-view=false`

Example migration structure:

```text
src/main/resources/db/migration

V1__initial_schema.sql
V2__...
```

This approach keeps the database schema version-controlled and reproducible.

---

## 👤 Users & Roles

The application includes a role-based user management system.

Planned roles include:

```text
ADMIN
USER
```

The authorization system will control which operations users are allowed to perform inside the warehouse system.

---

## 📦 Planned Features

WareFlow is being developed incrementally.

Planned modules include:

* User Management
* Role Management
* Authentication & Authorization
* Warehouse Management
* Storage Locations
* Inventory Management
* Products / Materials
* Pallet Management
* Goods Receipt
* Goods Issue
* Stock Movements
* Internal Material Transfers
* Warehouse Tasks
* Inventory History
* Audit Logging
* Error Handling
* REST API Documentation
* Reporting
* Dashboard

Additional features may be added during development.

---

## 🚚 Example Warehouse Workflow

A typical WareFlow process could look like:

```text
Goods arrive at warehouse
        │
        ▼
Goods Receipt
        │
        ▼
Material / Pallet registered
        │
        ▼
Storage location assigned
        │
        ▼
Warehouse task created
        │
        ▼
Material moved
        │
        ▼
Inventory updated
```

---

## 🔄 Database Migrations

Flyway automatically applies database migrations when the application starts.

Example:

```text
V1__create_initial_tables.sql
V2__add_roles_and_users.sql
```

Each database modification should be introduced through a new migration instead of manually changing the production schema.

---

## ▶️ Running the Project

### Requirements

Install:

* Java
* Maven
* PostgreSQL
* Git

Clone the repository:

```bash
git clone https://github.com/mohamad-web/WareFlow-Enterprise-Warehouse-Management-System-WMS-.git
```

Navigate into the project:

```bash
cd WareFlow-Enterprise-Warehouse-Management-System-WMS-
```

Configure PostgreSQL in:

```text
src/main/resources/application.properties
```

Example configuration:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/wareflow
spring.datasource.username=your_username
spring.datasource.password=your_password
```

Run the application:

```bash
mvn spring-boot:run
```

By default, the Spring Boot application runs on:

```text
http://localhost:8080
```

---

## 🧪 Tests

Run all tests with:

```bash
mvn test
```

The project includes automated tests that are expanded alongside new features.

---

## 🗺 Development Roadmap

### Phase 1 – Project Setup

* [x] Spring Boot project initialization
* [x] Maven configuration
* [x] PostgreSQL integration
* [x] Flyway integration
* [x] Database schema initialization

### Phase 2 – Domain & Persistence

* [x] Initial database design
* [x] Domain model
* [x] JPA entities
* [x] Repository layer

### Phase 3 – User Management

* [ ] Role entity
* [ ] User entity
* [ ] User management service
* [ ] Role management
* [ ] Authentication
* [ ] Authorization

### Phase 4 – Warehouse Core

* [ ] Warehouses
* [ ] Storage locations
* [ ] Materials / Products
* [ ] Inventory
* [ ] Pallets

### Phase 5 – Warehouse Operations

* [ ] Goods Receipt
* [ ] Goods Issue
* [ ] Material Movement
* [ ] Warehouse Tasks
* [ ] Inventory Transfers

### Phase 6 – Enterprise Features

* [ ] Audit logs
* [ ] Advanced validation
* [ ] Centralized exception handling
* [ ] API documentation
* [ ] Reporting
* [ ] Dashboard
* [ ] Docker deployment

---

## 🎯 Project Goals

The main goal of WareFlow is not only to build a working warehouse application, but also to demonstrate the design and implementation of an enterprise Java backend system.

The project demonstrates concepts such as:

* Object-Oriented Programming
* REST API Design
* Layered Architecture
* Relational Database Design
* JPA / Hibernate
* Database Migrations
* Role-Based Access Control
* Exception Handling
* Validation
* Testing
* Git Version Control
* Enterprise Application Architecture

---

## 📈 Current Status

WareFlow is currently under active development.

The infrastructure, database integration, initial database schema, and core domain structure have been implemented.

Development is currently focused on:

**Users, Roles, Authentication and Authorization.**

---

## 👨‍💻 Author

**Mohamad Khalaf Alnaser**

Computer Science / Software Development

GitHub:
`mohamad-web`

---

## 📄 License

This project is currently intended for educational purposes.

A formal open-source license may be added later.
