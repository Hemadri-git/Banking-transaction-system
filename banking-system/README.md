# 🏦 Banking Transaction System

A secure RESTful banking API built with **Java 17 · Spring Boot 3 · Spring Security · JWT · MySQL**.

Supports account creation, deposits, withdrawals, inter-account transfers, and paginated transaction history — all secured with JWT authentication and role-based access control.

---

## 📋 Table of Contents
- [Prerequisites](#prerequisites)
- [Step 1 — Install Prerequisites](#step-1--install-prerequisites)
- [Step 2 — Set Up MySQL](#step-2--set-up-mysql)
- [Step 3 — Configure the Application](#step-3--configure-the-application)
- [Step 4 — Build & Run](#step-4--build--run)
- [Step 5 — Test the API](#step-5--test-the-api)
- [Step 6 — Push to GitHub](#step-6--push-to-github)
- [API Reference](#api-reference)

---

## Prerequisites

| Tool | Version | Download |
|------|---------|----------|
| Java JDK | 17+ | https://adoptium.net |
| Maven | 3.8+ | https://maven.apache.org |
| MySQL | 8.0+ | https://dev.mysql.com/downloads |
| Git | any | https://git-scm.com |
| Postman (optional) | any | https://www.postman.com |

---

## Step 1 — Install Prerequisites

### Windows
```bash
# Install Java 17 (using winget)
winget install EclipseAdoptium.Temurin.17.JDK

# Install Maven
winget install Apache.Maven

# Install MySQL
winget install Oracle.MySQL
```

### macOS
```bash
brew install openjdk@17 maven mysql
brew services start mysql
```

### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install openjdk-17-jdk maven mysql-server -y
sudo systemctl start mysql
sudo systemctl enable mysql
```

### Verify installations
```bash
java -version        # should show 17.x.x
mvn -version         # should show 3.x.x
mysql --version      # should show 8.x.x
```

---

## Step 2 — Set Up MySQL

```bash
# Log in to MySQL as root
mysql -u root -p
# (press Enter if no password is set yet)
```

```sql
-- Create the database (app will auto-create tables)
CREATE DATABASE banking_db;

-- Optional: create a dedicated user instead of using root
CREATE USER 'bankuser'@'localhost' IDENTIFIED BY 'bankpass123';
GRANT ALL PRIVILEGES ON banking_db.* TO 'bankuser'@'localhost';
FLUSH PRIVILEGES;

EXIT;
```

---

## Step 3 — Configure the Application

Open `src/main/resources/application.properties` and update the credentials:

```properties
# If using root:
spring.datasource.username=root
spring.datasource.password=YOUR_ROOT_PASSWORD

# If you created a dedicated user:
spring.datasource.username=bankuser
spring.datasource.password=bankpass123
```

> ⚠️ Leave all other settings as-is for the first run.

---

## Step 4 — Build & Run

### Option A — Using Maven (command line)
```bash
# Navigate to the project root (where pom.xml is)
cd banking-system

# Clean build
mvn clean install -DskipTests

# Run the application
mvn spring-boot:run
```

### Option B — Using the JAR
```bash
mvn clean package -DskipTests
java -jar target/banking-system-1.0.0.jar
```

### Option C — From IntelliJ IDEA
1. Open IntelliJ → **File → Open** → select the `banking-system` folder
2. Wait for Maven to download dependencies (watch the bottom progress bar)
3. Open `BankingSystemApplication.java`
4. Click the green ▶ Run button

### ✅ Success output
```
Started BankingSystemApplication in 3.x seconds
✅ Default admin user created — username: admin, password: admin123
```

The API is now live at **http://localhost:8080**

---

## Step 5 — Test the API

### Using cURL

#### 1. Register a new customer
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "password123",
    "fullName": "John Doe"
  }'
```

#### 2. Login and get JWT token
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "john_doe", "password": "password123"}'
```
Copy the `token` value from the response. Use it as `YOUR_TOKEN` below.

#### 3. Create a bank account
```bash
curl -X POST http://localhost:8080/api/accounts \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"accountType": "SAVINGS", "initialDeposit": 1000.00}'
```
Copy the `accountNumber` from the response (e.g. `ACC0012345678`).

#### 4. Deposit funds
```bash
curl -X POST http://localhost:8080/api/transactions/deposit \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"accountNumber": "ACC0012345678", "amount": 500.00, "description": "Salary"}'
```

#### 5. Withdraw funds
```bash
curl -X POST http://localhost:8080/api/transactions/withdraw \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"accountNumber": "ACC0012345678", "amount": 200.00}'
```

#### 6. Transfer between accounts
```bash
curl -X POST http://localhost:8080/api/transactions/transfer \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "sourceAccountNumber": "ACC0012345678",
    "destinationAccountNumber": "ACC0098765432",
    "amount": 300.00,
    "description": "Rent payment"
  }'
```

#### 7. Get transaction history (paginated)
```bash
curl "http://localhost:8080/api/transactions/history/1?page=0&size=10" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

#### 8. Get history with date range filter
```bash
curl "http://localhost:8080/api/transactions/history/1?startDate=2024-01-01T00:00:00&endDate=2024-12-31T23:59:59" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

#### 9. Admin — view all users (login as admin first)
```bash
# Login as admin
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'

# Use admin token
curl http://localhost:8080/api/admin/users \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

---

## API Reference

### Authentication
| Method | Endpoint | Auth Required | Description |
|--------|----------|---------------|-------------|
| POST | `/api/auth/register` | ❌ | Register new customer |
| POST | `/api/auth/login` | ❌ | Login and get JWT token |

### Accounts
| Method | Endpoint | Auth Required | Description |
|--------|----------|---------------|-------------|
| POST | `/api/accounts` | ✅ Customer | Create a new account |
| GET | `/api/accounts/{accountNumber}` | ✅ Customer | Get account details |
| GET | `/api/accounts/my-accounts` | ✅ Customer | List my accounts |
| PATCH | `/api/accounts/{accountNumber}/close` | ✅ Customer | Close an account |

### Transactions
| Method | Endpoint | Auth Required | Description |
|--------|----------|---------------|-------------|
| POST | `/api/transactions/deposit` | ✅ Customer | Deposit funds |
| POST | `/api/transactions/withdraw` | ✅ Customer | Withdraw funds |
| POST | `/api/transactions/transfer` | ✅ Customer | Transfer between accounts |
| GET | `/api/transactions/history/{accountId}` | ✅ Customer | Paginated history + date filter |

### Admin (requires ADMIN role)
| Method | Endpoint | Auth Required | Description |
|--------|----------|---------------|-------------|
| GET | `/api/admin/users` | ✅ Admin | List all users |
| GET | `/api/admin/accounts` | ✅ Admin | List all accounts |
| PATCH | `/api/admin/accounts/{accountNumber}/suspend` | ✅ Admin | Suspend an account |

---

## 🏗️ Project Structure

```
banking-system/
├── src/main/java/com/banking/
│   ├── BankingSystemApplication.java
│   ├── config/
│   │   ├── SecurityConfig.java        # Spring Security + JWT setup
│   │   └── DataInitializer.java       # Seeds default admin user
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── AccountController.java
│   │   ├── TransactionController.java
│   │   └── AdminController.java
│   ├── dto/                           # Request/Response objects
│   ├── entity/                        # JPA entities (User, Account, Transaction)
│   ├── exception/                     # Custom exceptions + GlobalExceptionHandler
│   ├── repository/                    # Spring Data JPA repositories
│   ├── security/                      # JWT filter, UserDetails, JwtUtil
│   └── service/                       # Business logic layer
├── src/main/resources/
│   └── application.properties
├── pom.xml
└── README.md
```

---

## Default Credentials

| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `admin123` |

> 🔒 Change the admin password in production by calling `/api/auth/login` and updating via the database.
