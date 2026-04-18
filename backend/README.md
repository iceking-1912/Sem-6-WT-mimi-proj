# Backend – Online Book Store

Java Servlet + JDBC REST API packaged as a WAR file.

---

## Prerequisites

| Tool          | Version  |
|---------------|----------|
| Java JDK      | 11+      |
| Apache Maven  | 3.8+     |
| Apache Tomcat | 10+      |
| MySQL         | 8+       |

---

## Project Structure

```
backend/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/bookstore/
    │   │   ├── filter/
    │   │   │   └── CORSFilter.java          # Adds CORS headers to all responses
    │   │   ├── model/
    │   │   │   ├── Book.java
    │   │   │   ├── User.java
    │   │   │   └── CartItem.java
    │   │   ├── dao/
    │   │   │   ├── BookDAO.java             # CRUD operations for books
    │   │   │   ├── UserDAO.java             # Registration, login, uniqueness checks
    │   │   │   └── CartDAO.java             # Cart management
    │   │   ├── servlet/
    │   │   │   ├── BookServlet.java         # GET /api/books
    │   │   │   ├── UserServlet.java         # POST /api/users/register|login
    │   │   │   └── CartServlet.java         # GET|POST|PUT|DELETE /api/cart
    │   │   └── util/
    │   │       ├── DBConnection.java        # Opens JDBC connections
    │   │       └── PasswordUtil.java        # SHA-256 hashing & verification
    │   ├── resources/
    │   │   └── db.properties.example       # Template – copy to db.properties
    │   └── webapp/
    │       └── WEB-INF/web.xml
    └── test/
        └── java/com/bookstore/
            ├── model/                       # BookTest, UserTest, CartItemTest
            └── util/                        # PasswordUtilTest
```

---

## Setup

### 1. Database

Run the SQL scripts from the `database/` folder in your MySQL client:

```sql
source /path/to/database/schema.sql
source /path/to/database/sample_data.sql
```

### 2. Configure credentials

```bash
cp src/main/resources/db.properties.example src/main/resources/db.properties
# Edit db.properties and fill in your MySQL username and password
```

> **Important:** `db.properties` is in `.gitignore` and must **not** be committed.

### 3. Build

```bash
cd backend
mvn clean package
```

The WAR is written to `target/bookstore.war`.

### 4. Deploy

Copy `target/bookstore.war` to your Tomcat `webapps/` directory and start Tomcat.
The API is then available at `http://localhost:8080/bookstore/api/...`.

---

## API Reference

### Books

| Method | Endpoint              | Description                               |
|--------|-----------------------|-------------------------------------------|
| GET    | /api/books            | List all books                            |
| GET    | /api/books?search=kw  | Search by title or author (case-insensitive) |

### Users

| Method | Endpoint               | Body fields                    | Description     |
|--------|------------------------|--------------------------------|-----------------|
| POST   | /api/users/register    | username, email, password      | Create account  |
| POST   | /api/users/login       | username, password             | Authenticate    |

### Cart

| Method | Endpoint              | Params / Body                          | Description           |
|--------|-----------------------|----------------------------------------|-----------------------|
| GET    | /api/cart?userId={id} | –                                      | Fetch cart            |
| POST   | /api/cart             | { userId, bookId, quantity }           | Add / increment item  |
| PUT    | /api/cart             | { cartId, quantity }                   | Set quantity          |
| DELETE | /api/cart?cartId={id} | –                                      | Remove item           |

All responses are JSON.  Error responses have shape `{ "error": "..." }`.

---

## Validation Rules

| Field    | Rule                                                    |
|----------|---------------------------------------------------------|
| username | 3–50 chars, letters / digits / underscore only          |
| email    | Valid email format                                      |
| password | Minimum 6 characters                                   |
| quantity | Integer ≥ 1                                             |

Validation is enforced in **both** the frontend and backend.

---

## Running Tests

```bash
cd backend
mvn test
```

Tests cover model classes and `PasswordUtil` (no external DB required).
