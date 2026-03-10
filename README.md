# 💰 Expense Tracker API

A production-grade RESTful API for personal expense management built with Java Spring Boot, featuring JWT authentication, Redis-based session management, pagination, Excel export, and Docker support.

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)
![Redis](https://img.shields.io/badge/Redis-Blacklisting-red)
![Docker](https://img.shields.io/badge/Docker-Containerized-blue)
![Tests](https://img.shields.io/badge/Tests-10%20Passing-brightgreen)

---

## ✨ Features

- ✅ User Registration & Login with JWT Authentication
- ✅ JWT Logout with Redis-based Token Blacklisting
- ✅ Expense & Category CRUD APIs
- ✅ Pagination & Sorting for expense listing
- ✅ Filter expenses by category and date range
- ✅ Category-wise expense summary with total
- ✅ Excel export for expense reports
- ✅ Standard API response wrapper with centralized exception handling
- ✅ API Documentation with Swagger OpenAPI + JWT support
- ✅ Dockerized with Docker Compose
- ✅ Unit tested with JUnit 5 & Mockito

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 21 |
| Framework | Spring Boot 4.x |
| Security | Spring Security + JWT |
| Session Management | Redis (Token Blacklisting) |
| Database | PostgreSQL |
| ORM | Spring Data JPA + Hibernate |
| Excel | Apache POI |
| Testing | JUnit 5 + Mockito |
| Documentation | Swagger OpenAPI 3 |
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
Swagger UI at `http://localhost:9091/swagger-ui/index.html` ✅

### Option 2 — Run Locally

**Prerequisites:** Java 21, PostgreSQL, Redis, Maven
```bash
# Clone the repo
git clone https://github.com/prrernaa/expense-tracker-api.git
cd expense-tracker-api

# Start Redis (WSL or local)
sudo service redis-server start

# Setup properties
cp src/main/resources/application-example.properties src/main/resources/application.properties
# Update DB credentials in application.properties

# Run
mvn spring-boot:run
```

---

## 📖 API Documentation

Swagger UI: `http://localhost:9091/swagger-ui/index.html`

To test protected APIs in Swagger:
1. Call `/api/auth/login` → copy the token
2. Click **Authorize** button (top right)
3. Paste token and click Authorize ✅

---

## 📡 API Endpoints

### Auth
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | /api/auth/register | Register new user | ❌ |
| POST | /api/auth/login | Login and get JWT token | ❌ |
| POST | /api/auth/logout | Logout and blacklist token | ✅ |

### Categories
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | /api/categories | Create category | ✅ |
| GET | /api/categories | Get all categories | ✅ |
| DELETE | /api/categories/{id} | Delete category | ✅ |

### Expenses
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | /api/expenses | Create expense | ✅ |
| GET | /api/expenses | Get expenses (paginated) | ✅ |
| GET | /api/expenses/{id} | Get expense by ID | ✅ |
| PUT | /api/expenses/{id} | Update expense | ✅ |
| DELETE | /api/expenses/{id} | Delete expense | ✅ |
| GET | /api/expenses/filter | Filter by category/date | ✅ |
| GET | /api/expenses/summary | Category-wise summary | ✅ |
| GET | /api/expenses/export | Export to Excel | ✅ |

---

## 🔍 Query Parameters

### Pagination & Sorting
```
GET /api/expenses?page=0&size=10&sortBy=date&sortDir=desc
```

| Param | Default | Options |
|-------|---------|---------|
| page | 0 | any number |
| size | 10 | any number |
| sortBy | date | date, amount, title |
| sortDir | desc | asc, desc |

### Filtering
```
GET /api/expenses/filter?categoryId=1
GET /api/expenses/filter?startDate=2026-01-01&endDate=2026-03-31
GET /api/expenses/filter?categoryId=1&startDate=2026-01-01&endDate=2026-03-31
```

---

## 📦 Sample Requests & Responses

### Register
```json
POST /api/auth/register
{
  "name": "Prerna",
  "email": "prerna@gmail.com",
  "password": "prerna123"
}
```
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

### Create Expense
```json
POST /api/expenses
{
  "title": "Lunch",
  "amount": 250.00,
  "date": "2026-03-01",
  "note": "Ate at cafe",
  "categoryId": 1
}
```
```json
{
  "status": 201,
  "message": "Expense created successfully",
  "body": {
    "id": 1,
    "title": "Lunch",
    "amount": 250.00,
    "date": "2026-03-01",
    "note": "Ate at cafe",
    "categoryName": "Food"
  }
}
```

### Expense Summary
```json
GET /api/expenses/summary
```
```json
{
  "status": 200,
  "message": "Summary fetched successfully",
  "body": {
    "Food": 5000.00,
    "Travel": 3000.00,
    "Bills": 8000.00,
    "Total": 16000.00
  }
}
```

### Logout
```json
POST /api/auth/logout
Authorization: Bearer your_token
```
```json
{
  "status": 200,
  "message": "Logged out successfully",
  "body": null
}
```

---

## 🏗️ Project Structure
```
src/main/java/com/prerna/expense_tracker/
├── controller/          # REST API endpoints
│   ├── AuthController
│   ├── CategoryController
│   └── ExpenseController
├── service/             # Business logic
│   ├── AuthService
│   ├── CategoryService
│   ├── ExpenseService
│   ├── ExcelExportService
│   ├── TokenBlacklistService
│   └── CustomUserDetailsService
├── repository/          # Database operations
│   ├── UserRepository
│   ├── CategoryRepository
│   └── ExpenseRepository
├── entity/              # JPA entities
│   ├── User
│   ├── Category
│   └── Expense
├── dto/                 # Request/Response objects
│   ├── ApiResponse
│   ├── PaginatedResponse
│   ├── AuthResponse
│   ├── ExpenseRequest/Response
│   └── CategoryRequest/Response
├── security/            # JWT filter & config
│   ├── JwtUtil
│   ├── JwtAuthFilter
│   └── SecurityConfig
├── config/              # App configuration
│   └── SwaggerConfig
└── exception/           # Global exception handler
    └── GlobalExceptionHandler
```

---

## 🧪 Running Tests
```bash
mvn test
```

10 unit tests covering `AuthService` and `ExpenseService` using JUnit 5 and Mockito. ✅

---

## 🔐 Security Architecture
```
Request → JwtAuthFilter
              ↓
    Is token blacklisted in Redis?
         YES → 401 Unauthorized
          NO → Is token valid?
                YES → Set Authentication → Controller
                 NO → 401 Unauthorized
```

---

## 🐳 Docker Setup

The `docker-compose.yml` spins up:
- **PostgreSQL** on port 5433
- **Spring Boot App** on port 9091

Both are connected via a Docker network — no manual DB setup needed.
```bash
docker-compose up --build    # Start
docker-compose down          # Stop
docker-compose down -v       # Stop and remove data
```