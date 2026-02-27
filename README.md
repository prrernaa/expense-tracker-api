# Expense Tracker API

A production-grade REST API built with Java Spring Boot for personal expense management.

## Tech Stack
- Java 21, Spring Boot 4.x
- Spring Security + JWT Authentication
- PostgreSQL + Spring Data JPA + Hibernate
- Apache POI (Excel Export)
- Lombok

## Features
- User Registration & Login with JWT
- Expense CRUD with category management
- Filter expenses by category and date range
- Export expenses to Excel
- Centralized exception handling

## Getting Started

### Prerequisites
- Java 21
- PostgreSQL
- Maven

### Setup
1. Clone the repo
2. Create a PostgreSQL database named `expense_tracker_db`
3. Copy `application-example.properties` to `application.properties`
4. Update DB credentials in `application.properties`
5. Run `mvn spring-boot:run`

## API Endpoints

### Auth
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/auth/register | Register new user |
| POST | /api/auth/login | Login and get JWT token |

### Expenses (coming soon)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/expenses | Create expense |
| GET | /api/expenses | Get all expenses |
| PUT | /api/expenses/{id} | Update expense |
| DELETE | /api/expenses/{id} | Delete expense |
| GET | /api/expenses/export | Export to Excel |
