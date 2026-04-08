# FoodTrace — Web-based Food Waste & Inventory Analytics System

**Monroe University · CS620 · Spring 2026**  
Author: Opoku Duah

---

## Tech Stack

| Layer     | Technology                              |
|-----------|-----------------------------------------|
| Frontend  | HTML5, Tailwind CSS, JavaScript, jQuery |
| Backend   | Java 17, Spring Boot 3.2, Spring Security (JWT + RBAC) |
| Database  | MySQL 8+                                |
| Testing   | JUnit 5, Mockito, MockMvc               |
| Deploy    | Docker + AWS (optional)                 |

---

## Project Structure

```
foodwaste-system/
├── backend/                        # Spring Boot API
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/foodwaste/
│       │   │   ├── FoodWasteApplication.java
│       │   │   ├── config/         # JWT, Security, CORS
│       │   │   ├── controller/     # REST endpoints
│       │   │   ├── dto/            # Request/response objects
│       │   │   ├── exception/      # Custom exceptions + global handler
│       │   │   ├── model/          # JPA entities
│       │   │   ├── repository/     # Spring Data JPA repos
│       │   │   └── service/        # Business logic
│       │   └── resources/
│       │       ├── application.properties
│       │       └── schema.sql      # MySQL DDL + seed data
│       └── test/
│           ├── java/com/foodwaste/
│           │   ├── controller/     # MockMvc integration tests
│           │   └── service/        # Mockito unit tests
│           └── resources/
│               └── application.properties  # H2 test config
└── frontend/                       # HTML/CSS/JS pages
    ├── index.html          # Login / Register
    ├── dashboard.html      # Analytics dashboard
    ├── inventory.html      # Inventory management
    ├── waste-logs.html     # Log food waste
    ├── alerts.html         # Alert threshold config
    ├── users.html          # User management (Admin)
    └── js/
        └── app.js          # Shared API client + utilities
```

---

## Prerequisites

- Java 17+
- Maven 3.8+
- MySQL 8+
- A modern browser (Chrome, Firefox, Edge)

---

## Setup & Run

### 1. MySQL Database

```bash
# Log in to MySQL
mysql -u root -p

# Run the schema + seed script
source backend/src/main/resources/schema.sql
```

This creates the `foodwaste_db` database with all tables and sample data.

**Default seed accounts** (password: `password123`):

| Username | Role    |
|----------|---------|
| admin    | ADMIN   |
| manager  | MANAGER |
| staff01  | STAFF   |
| staff02  | STAFF   |

---

### 2. Backend (Spring Boot)

```bash
cd backend

# Set environment variables (or edit application.properties directly)
export DB_USERNAME=root
export DB_PASSWORD=your_mysql_password
export JWT_SECRET=c2VjcmV0S2V5Rm9yRm9vZFdhc3RlU3lzdGVtMjAyNlN1cGVyU2VjdXJlS2V5MTIz

# Build and run
mvn clean install
mvn spring-boot:run
```

API will be available at: **http://localhost:8080/api**

---

### 3. Frontend

The frontend is plain HTML/JS — no build step required.

Option A — Open directly in browser:
```bash
open frontend/index.html
```

Option B — Serve with a local dev server:
```bash
# Using Python
cd frontend && python3 -m http.server 5500

# Using Node.js / npx
cd frontend && npx serve .
```

Then visit **http://localhost:5500**

---

### 4. Run Tests

```bash
cd backend
mvn test
```

Tests use an **H2 in-memory database** — no MySQL needed to run tests.

---

## REST API Reference

All endpoints require `Authorization: Bearer <token>` unless marked public.

### Auth (Public)
| Method | Endpoint            | Description              |
|--------|---------------------|--------------------------|
| POST   | `/api/auth/login`   | Authenticate, get JWT    |
| POST   | `/api/auth/register`| Create new user account  |

### Inventory (Staff: read · Manager: write · Admin: delete)
| Method | Endpoint                    | Description           |
|--------|-----------------------------|-----------------------|
| GET    | `/api/inventory`            | List all items        |
| GET    | `/api/inventory/{id}`       | Get item by ID        |
| GET    | `/api/inventory/low-stock`  | Items below threshold |
| GET    | `/api/inventory/expired`    | Expired items         |
| POST   | `/api/inventory`            | Create item           |
| PUT    | `/api/inventory/{id}`       | Update item           |
| DELETE | `/api/inventory/{id}`       | Delete item           |

### Waste Logs (All roles: create · Manager+: read all)
| Method | Endpoint                        | Description              |
|--------|---------------------------------|--------------------------|
| POST   | `/api/waste-logs`               | Submit waste log         |
| GET    | `/api/waste-logs`               | List all logs            |
| GET    | `/api/waste-logs/{id}`          | Get log by ID            |
| GET    | `/api/waste-logs/by-item/{id}`  | Logs for a specific item |
| GET    | `/api/waste-logs/by-date`       | Filter by date range     |

### Alerts (Manager+)
| Method | Endpoint              | Description            |
|--------|-----------------------|------------------------|
| GET    | `/api/alerts`         | List all thresholds    |
| GET    | `/api/alerts/item/{id}` | Threshold by item    |
| POST   | `/api/alerts`         | Create threshold       |
| PUT    | `/api/alerts/{id}`    | Update threshold       |
| DELETE | `/api/alerts/{id}`    | Delete threshold       |

### Analytics (Manager+)
| Method | Endpoint                 | Description          |
|--------|--------------------------|----------------------|
| GET    | `/api/analytics/summary` | Dashboard KPIs       |

### Users (Admin only)
| Method | Endpoint                    | Description       |
|--------|-----------------------------|-------------------|
| GET    | `/api/users`                | List all users    |
| GET    | `/api/users/{id}`           | Get user by ID    |
| PUT    | `/api/users/{id}/role`      | Change user role  |
| DELETE | `/api/users/{id}`           | Delete user       |

---

## RBAC Summary

| Feature               | STAFF | MANAGER | ADMIN |
|-----------------------|:-----:|:-------:|:-----:|
| View inventory        | ✓     | ✓       | ✓     |
| Log waste             | ✓     | ✓       | ✓     |
| Create/edit inventory | ✗     | ✓       | ✓     |
| Delete inventory      | ✗     | ✗       | ✓     |
| View all waste logs   | ✗     | ✓       | ✓     |
| Manage alerts         | ✗     | ✓       | ✓     |
| View analytics        | ✗     | ✓       | ✓     |
| Manage users          | ✗     | ✗       | ✓     |

---

## Risk Mitigations Implemented

| Risk                    | Mitigation                                           |
|-------------------------|------------------------------------------------------|
| High latency            | Transactional read-only queries; analytics grouping done in DB |
| Database lock contention| `@Transactional` with appropriate isolation; Spring Data JPA |
| Frontend fragility      | Tailwind CSS for cross-browser compatibility         |
| Auth vulnerabilities    | JWT HS256, BCrypt passwords, Spring Security RBAC    |
| Data validation         | `@Valid` + Jakarta Bean Validation on all DTOs       |
| Error handling          | Global `@RestControllerAdvice` exception handler     |

---

## Docker (Optional)

```bash
# Build JAR
cd backend && mvn clean package -DskipTests

# Build Docker image
docker build -t foodtrace-api .

# Run with env vars
docker run -p 8080:8080 \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=password \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/foodwaste_db \
  foodtrace-api
```

---

*CS620-153HY · Software System Design · Monroe University · 2026*
