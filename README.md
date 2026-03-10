# 💰 Expense Tracker API

A production-grade RESTful API for personal expense management built with Java Spring Boot.

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)
![Docker](https://img.shields.io/badge/Docker-Containerized-blue)

---

## ✨ Features

- JWT-based Authentication & Authorization
- Expense CRUD with category management
- Filter expenses by category and date range
- Excel export for expense reports
- Centralized exception handling with standard API response structure
- Dockerized with Docker Compose
- Unit tested with JUnit 5 & Mockito

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 21 |
| Framework | Spring Boot 4.x |
| Security | Spring Security + JWT |
| Database | PostgreSQL |
| ORM | Spring Data JPA + Hibernate |
| Excel | Apache POI |
| Testing | JUnit 5 + Mockito |
| DevOps | Docker + Docker Compose |

---

## 🚀 Getting Started

### Option 1 — Run with Docker (Recommended)
```bash
# Clone the repo
git clone https://github.com/prrernaa/expense-tracker-api.git
cd expense-tracker-api

# Start everything with one command
docker-compose up --build
```

App runs at `http://localhost:9091` ✅

### Option 2 — Run Locally

**Prerequisites:** Java 21, PostgreSQL, Maven
```bash
# Clone the repo
git clone https://github.com/prrernaa/expense-tracker-api.git
cd expense-tracker-api

# Setup properties
cp src/main/resources/application-example.properties src/main/resources/application.properties
# Update DB credentials in application.properties

# Run
mvn spring-boot:run
```

---

## 📡 API Endpoints

### Auth
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | /api/auth/register | Register new user | ❌ |
| POST | /api/auth/login | Login and get JWT | ❌ |

### Categories
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | /api/categories | Create category | ✅ |
| GET | /api/categories | Get all categories | ✅ |
| DELETE | /api/categories/{id} | Delete category | ✅ |

### Expenses
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | /api/expenses | Create expense | ✅ |
| GET | /api/expenses | Get all expenses | ✅ |
| GET | /api/expenses/{id} | Get expense by ID | ✅ |
| PUT | /api/expenses/{id} | Update expense | ✅ |
| DELETE | /api/expenses/{id} | Delete expense | ✅ |
| GET | /api/expenses/filter | Filter expenses | ✅ |
| GET | /api/expenses/export | Export to Excel | ✅ |

### Filter Parameters
```
GET /api/expenses/filter?categoryId=1
GET /api/expenses/filter?startDate=2026-01-01&endDate=2026-03-31
GET /api/expenses/filter?categoryId=1&startDate=2026-01-01&endDate=2026-03-31
```

---

## 📦 Sample API Usage

**Register:**
```json
POST /api/auth/register
{
  "name": "Prerna",
  "email": "prerna@gmail.com",
  "password": "prerna123"
}
```

**Response:**
```json
{
  "status": 201,
  "message": "User registered successfully",
  "body": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "email": "prerna@gmail.com",
    "name": "Prerna"
  }
}
```

---

## 🧪 Running Tests
```bash
mvn test
```

10 unit tests covering AuthService and ExpenseService. ✅

---

## 🏗️ Project Structure
```
src/main/java/com/prerna/expense_tracker/
├── controller/      # REST API endpoints
├── service/         # Business logic
├── repository/      # Database operations
├── entity/          # JPA entities
├── dto/             # Request/Response objects
├── security/        # JWT filter & config
└── exception/       # Global exception handler
```

## 📖 API Documentation
Swagger UI available at: `http://localhost:9091/swagger-ui/index.html