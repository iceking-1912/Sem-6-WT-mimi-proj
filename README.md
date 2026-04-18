# Online Book Store – Mini Project

A dynamic web application for browsing, searching, and purchasing books online.

## Project Structure

```
├── frontend/      # HTML, CSS, JavaScript UI
├── backend/       # Java Servlet + JDBC backend (WAR)
├── database/      # MySQL schema and sample data
└── README.md
```

## Technologies

| Layer     | Technology                          |
|-----------|-------------------------------------|
| Frontend  | HTML5, CSS3, JavaScript (Vanilla)   |
| Backend   | Java Servlets, JDBC                 |
| Database  | MySQL 8+                            |
| Build     | Apache Maven 3.8+                   |

## Quick Start

1. **Database** – run `database/schema.sql` then `database/sample_data.sql` in MySQL.
2. **Backend** – configure `backend/src/main/resources/db.properties`, then `cd backend && mvn package` and deploy the WAR to Tomcat 10 (or `mvn tomcat7:run`).
3. **Frontend** – open `frontend/index.html` in a browser (or serve via any static file server).

See `frontend/README.md` and `backend/README.md` for detailed instructions.

## Features

- Browse book catalog (title, author, price, cover image)
- Search books by title or author
- User registration and login (password hashed with SHA-256)
- Add to cart, update quantity, remove items
- Persistent cart tied to logged-in user
- Input validation on both frontend and backend
- CORS-enabled REST API