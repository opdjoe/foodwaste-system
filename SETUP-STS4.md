# Opening FoodTrace in Spring Tool Suite 4

## Step-by-step import guide

---

### Prerequisites

Before opening in STS4, make sure you have:

- **Spring Tool Suite 4** (4.20+ recommended) — https://spring.io/tools
- **Java 17 JDK** — https://adoptium.net  
  *(In STS4: Window → Preferences → Java → Installed JREs → add Java 17)*
- **MySQL 8+** running locally on port 3306
- **Lombok plugin installed in STS4**  
  *(Required — the project uses `@Data`, `@Builder`, etc.)*

---

### 1. Install the Lombok Plugin

> Skip this if you already have Lombok set up in STS4.

1. Download `lombok.jar` from https://projectlombok.org/download
2. Double-click `lombok.jar` to run the installer
3. Point it at your `SpringToolSuite4.exe` / `SpringToolSuite4.app`
4. Click **Install / Update**, then restart STS4

---

### 2. Set Up the MySQL Database

1. Open MySQL Workbench (or any MySQL client)
2. Run the file: `backend/src/main/resources/schema.sql`

   ```sql
   SOURCE /path/to/foodwaste-system/backend/src/main/resources/schema.sql;
   ```

   This creates:
   - Database: `foodwaste_db`
   - Tables: `user`, `inventory`, `alert_threshold`, `waste_logs`
   - Sample seed data (users, items, logs)

**Default seed accounts** (all use password `password123`):

| Username | Role    |
|----------|---------|
| admin    | ADMIN   |
| manager  | MANAGER |
| staff01  | STAFF   |
| staff02  | STAFF   |

---

### 3. Edit Database Credentials

Open `backend/src/main/resources/application.properties` and update:

```properties
spring.datasource.username=root       # ← your MySQL username
spring.datasource.password=password   # ← your MySQL password
```

---

### 4. Import the Project into STS4

1. Open STS4
2. Go to **File → Import → Maven → Existing Maven Projects**
3. Click **Browse** and navigate to the `backend/` folder  
   *(select the folder that contains `pom.xml`)*
4. STS4 will detect `foodwaste-api` automatically
5. Click **Finish**
6. Wait for Maven to download all dependencies  
   *(watch the progress bar in the bottom-right corner)*

> **Tip:** If you see red markers after import, right-click the project →  
> **Maven → Update Project** (Alt+F5) → check **Force Update** → OK

---

### 5. Run the Application

**Option A — Boot Dashboard (recommended):**
1. Open **Window → Show View → Other → Spring → Spring Boot Dashboard**
2. You will see `foodwaste-api` listed
3. Click the **▶ Start** (play) button

**Option B — Run As:**
1. Right-click `FoodWasteApplication.java`
2. Select **Run As → Spring Boot App**

The API starts at: **http://localhost:8080**

---

### 6. Run the Tests

1. Right-click the project
2. Select **Run As → JUnit Test**

All tests use an **H2 in-memory database** — no MySQL connection needed.

Test classes:
- `FoodWasteApplicationTests` — context smoke test
- `InventoryServiceTest` — 9 unit tests
- `WasteLogServiceTest` — 5 unit tests
- `AlertThresholdServiceTest` — 7 unit tests
- `InventoryControllerTest` — 9 MockMvc integration tests

---

### 7. Open the Frontend

The frontend needs no build step. Open `frontend/index.html` in your browser.

For the best experience, serve it with a local server (avoids CORS issues):

```bash
# Option A: Python (if installed)
cd frontend
python -m http.server 5500

# Option B: Node.js live-server (if installed)
npx live-server frontend --port=5500
```

Then visit: **http://localhost:5500**

Login with: `admin` / `password123`

---

### Project Structure in STS4

After import, the Package Explorer will show:

```
foodwaste-api
├── src/main/java
│   └── com.foodwaste
│       ├── FoodWasteApplication.java   ← main class
│       ├── config/                     ← JWT + Security
│       ├── controller/                 ← REST endpoints
│       ├── dto/                        ← request/response objects
│       ├── exception/                  ← error handling
│       ├── model/                      ← JPA entities
│       ├── repository/                 ← data access
│       └── service/                    ← business logic
├── src/main/resources
│   ├── application.properties          ← main config (edit DB here)
│   ├── application-dev.properties      ← dev profile
│   ├── application-prod.properties     ← prod profile
│   └── schema.sql                      ← DB setup script
├── src/test/java
│   └── com.foodwaste
│       ├── FoodWasteApplicationTests.java
│       ├── controller/
│       └── service/
├── src/test/resources
│   └── application.properties          ← H2 test config
├── pom.xml
└── JRE System Library [JavaSE-17]
```

---

### Common Issues & Fixes

| Problem | Fix |
|---------|-----|
| Red error markers everywhere | Right-click project → Maven → Update Project (Alt+F5) |
| `@Data` / `@Builder` not resolved | Install Lombok plugin (Step 1 above) |
| `Communications link failure` | MySQL is not running, or wrong credentials in `application.properties` |
| Port 8080 already in use | Change `server.port=8081` in `application.properties` |
| Tests fail with DB errors | Tests use H2 — check `src/test/resources/application.properties` exists |
| Frontend shows CORS error | Ensure `http://127.0.0.1:5500` is in `app.cors.allowed-origins` |

---

*CS620-153HY · Software System Design · Monroe University · Spring 2026*
