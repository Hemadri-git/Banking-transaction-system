# 🏦 Banking Transaction System

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.0-6DB33F?style=for-the-badge&logo=springboot)
![Spring Security](https://img.shields.io/badge/Spring_Security-JWT-6DB33F?style=for-the-badge&logo=springsecurity)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.8+-C71A36?style=for-the-badge&logo=apachemaven)

A production-ready, secure RESTful Banking API built with **Spring Boot 3**, **Spring Security**, **JWT Authentication**, and **MySQL**. Supports account management, fund transactions, and role-based access control.

</div>

---

## 📌 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [API Endpoints](#-api-endpoints)
- [Security](#-security)
- [Getting Started](#-getting-started)
- [Testing with Postman](#-testing-with-postman)
- [Project Structure](#-project-structure)
- [Key Implementations](#-key-implementations)

---

## ✨ Features

- 🔐 **JWT Authentication** — Stateless token-based login & registration
- 👥 **Role-Based Access Control** — `CUSTOMER` and `ADMIN` roles
- 🏦 **Account Management** — Create, view, and close bank accounts (Savings / Checking / Fixed Deposit)
- 💸 **Fund Operations** — Deposit, Withdraw, and Transfer between accounts
- ⚡ **ACID Transactions** — `@Transactional` ensures data consistency on concurrent operations
- 📊 **Transaction History** — Paginated results with date-range filtering
- 🛡️ **Custom Exception Handling** — Meaningful error responses for all edge cases
- 🌐 **API Welcome Page** — Clean status page at `localhost:8080`

---

## 🛠 Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.2.0 |
| Security | Spring Security + JWT (JJWT 0.11.5) |
| Database | MySQL 8.0 |
| ORM | Spring Data JPA / Hibernate |
| Build Tool | Maven |
| Validation | Spring Boot Validation |
| Boilerplate | Lombok |

---

## 🏗 Architecture

```
Client (Postman / Frontend)
        │
        ▼
┌─────────────────────┐
│   JWT Auth Filter   │  ← Validates Bearer token on every request
└─────────────────────┘
        │
        ▼
┌─────────────────────┐
│    Controllers      │  ← Auth, Account, Transaction, Admin
└─────────────────────┘
        │
        ▼
┌─────────────────────┐
│     Services        │  ← Business logic, @Transactional
└─────────────────────┘
        │
        ▼
┌─────────────────────┐
│   Repositories      │  ← Spring Data JPA
└─────────────────────┘
        │
        ▼
┌─────────────────────┐
│      MySQL DB       │  ← users, accounts, transactions
└─────────────────────┘
```

---

## 📡 API Endpoints

### 🔐 Authentication — Public

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/auth/register` | Register a new customer |
| `POST` | `/api/auth/login` | Login and receive JWT token |

### 🏦 Accounts — Requires JWT

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/accounts` | Create a new bank account |
| `GET` | `/api/accounts/my-accounts` | Get all accounts for logged-in user |
| `GET` | `/api/accounts/{accountNumber}` | Get account details |
| `PATCH` | `/api/accounts/{accountNumber}/close` | Close an account |

### 💸 Transactions — Requires JWT

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/transactions/deposit` | Deposit funds into account |
| `POST` | `/api/transactions/withdraw` | Withdraw funds from account |
| `POST` | `/api/transactions/transfer` | Transfer between accounts |
| `GET` | `/api/transactions/history/{accountId}` | Paginated history with optional date filter |

### 👑 Admin — Requires ADMIN Role

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/admin/users` | List all registered users |
| `GET` | `/api/admin/accounts` | List all bank accounts |
| `PATCH` | `/api/admin/accounts/{accountNumber}/suspend` | Suspend an account |

---

## 🔒 Security

- All endpoints (except `/api/auth/**`) require a valid **JWT Bearer Token**
- Tokens expire after **24 hours**
- Passwords are encrypted using **BCrypt**
- Admin endpoints are protected by **`@PreAuthorize("hasRole('ADMIN')")`**
- Stateless session management — no server-side sessions

**Authorization Header format:**
```
Authorization: Bearer <your_jwt_token>
```

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- MySQL 8.0+

### 1. Clone the Repository

```bash
git clone https://github.com/YOUR_USERNAME/banking-transaction-system.git
cd banking-transaction-system
```

### 2. Create the Database

```sql
CREATE DATABASE banking_db;
```

### 3. Configure Application

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/banking_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

### 4. Build & Run

```bash
mvn clean install -DskipTests
mvn spring-boot:run
```

The server starts at **http://localhost:8080** ✅

> On first run, a default admin account is auto-created:
> - Username: `admin` | Password: `admin123`

---

## 🧪 Testing with Postman

### Step 1 — Login
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

Copy the `token` from the response.

### Step 2 — Authorize
In Postman → **Authorization tab** → **Bearer Token** → paste your token.

### Step 3 — Create Account
```http
POST http://localhost:8080/api/accounts
Authorization: Bearer <token>
Content-Type: application/json

{
  "accountType": "SAVINGS",
  "initialDeposit": 1000.00
}
```

### Step 4 — Deposit
```http
POST http://localhost:8080/api/transactions/deposit
Authorization: Bearer <token>
Content-Type: application/json

{
  "accountNumber": "ACC0012345678",
  "amount": 500.00,
  "description": "Salary credit"
}
```

### Step 5 — Transfer
```http
POST http://localhost:8080/api/transactions/transfer
Authorization: Bearer <token>
Content-Type: application/json

{
  "sourceAccountNumber": "ACC0012345678",
  "destinationAccountNumber": "ACC0098765432",
  "amount": 200.00,
  "description": "Rent payment"
}
```

### Step 6 — Transaction History with Date Filter
```http
GET http://localhost:8080/api/transactions/history/1?page=0&size=10&startDate=2024-01-01T00:00:00&endDate=2024-12-31T23:59:59
Authorization: Bearer <token>
```

---

## 📁 Project Structure

```
src/main/java/com/banking/
├── BankingSystemApplication.java
│
├── config/
│   ├── SecurityConfig.java          # Spring Security + JWT configuration
│   └── DataInitializer.java         # Seeds default admin on startup
│
├── controller/
│   ├── AuthController.java          # Register & Login endpoints
│   ├── AccountController.java       # Account CRUD endpoints
│   ├── TransactionController.java   # Deposit, Withdraw, Transfer endpoints
│   └── AdminController.java        # Admin-only endpoints
│
├── service/
│   ├── AuthService.java             # Authentication business logic
│   ├── AccountService.java          # Account business logic
│   └── TransactionService.java     # Transaction logic with @Transactional
│
├── entity/
│   ├── User.java                    # User entity (CUSTOMER / ADMIN roles)
│   ├── Account.java                 # Account entity (SAVINGS / CHECKING / FIXED_DEPOSIT)
│   └── Transaction.java            # Transaction entity (DEPOSIT / WITHDRAWAL / TRANSFER)
│
├── repository/
│   ├── UserRepository.java
│   ├── AccountRepository.java
│   └── TransactionRepository.java  # Custom queries with date-range filter
│
├── security/
│   ├── JwtUtil.java                 # JWT generation & validation
│   ├── JwtAuthFilter.java           # JWT filter for every request
│   ├── CustomUserDetails.java
│   └── CustomUserDetailsService.java
│
├── dto/                             # Request / Response objects
│   ├── RegisterRequest.java
│   ├── LoginRequest.java
│   ├── AuthResponse.java
│   ├── CreateAccountRequest.java
│   ├── AccountResponse.java
│   ├── DepositRequest.java
│   ├── WithdrawalRequest.java
│   ├── TransferRequest.java
│   ├── TransactionResponse.java
│   └── UserResponse.java
│
└── exception/
    ├── InsufficientFundsException.java
    ├── AccountNotFoundException.java
    ├── UserNotFoundException.java
    └── GlobalExceptionHandler.java  # Centralized error handling
```

---

## 💡 Key Implementations

### ACID-Compliant Fund Transfer
```java
@Transactional
public TransactionResponse transfer(TransferRequest request) {
    Account source = accountService.findByNumber(request.getSourceAccountNumber());
    Account destination = accountService.findByNumber(request.getDestinationAccountNumber());
    validateSufficientFunds(source, request.getAmount());
    source.setBalance(source.getBalance().subtract(request.getAmount()));
    destination.setBalance(destination.getBalance().add(request.getAmount()));
    // If anything fails here, both changes are rolled back automatically
    return TransactionResponse.from(transactionRepository.save(transaction));
}
```

### Custom Exception Handling
```java
@ExceptionHandler(InsufficientFundsException.class)
public ResponseEntity<ErrorResponse> handleInsufficientFunds(InsufficientFundsException ex) {
    return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
}
```

### Paginated History with Date Filter
```java
@Query("SELECT t FROM Transaction t WHERE " +
       "(t.sourceAccount.id = :accountId OR t.destinationAccount.id = :accountId) " +
       "AND t.createdAt BETWEEN :startDate AND :endDate")
Page<Transaction> findByAccountIdAndDateRange(...);
```

---

## 👤 Default Credentials

| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `admin123` |

---

<div align="center">
  <p>Built with ❤️ using Spring Boot</p>
</div>
