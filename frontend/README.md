# Frontend – Online Book Store

Pure HTML5, CSS3 and Vanilla JavaScript single-page site.
No external frameworks, build tools, or CSS libraries are used.

---

## File Structure

```
frontend/
├── index.html        # Home page – book catalog & search
├── login.html        # Login page
├── register.html     # Registration page
├── cart.html         # Shopping cart page
├── css/
│   └── style.css     # All styles (responsive, no external libs)
├── js/
│   ├── auth.js       # Session management, login/register logic
│   ├── main.js       # Book listing, search, add-to-cart
│   └── cart.js       # Cart display, quantity updates, remove items
└── README.md
```

---

## Pages

### index.html – Book Catalog
- Loads all books from the API on page load.
- Search bar filters by title or author (API-side search).
- Each book card shows cover image, title, author, category and price.
- **Add to Cart** button requires the user to be logged in.

### login.html – Login
- Username and password fields with real-time validation.
- On success, stores the user object in `localStorage` and redirects to the home page.

### register.html – Register
- Username, email, password and confirm-password fields with real-time validation.
- Validation rules match the backend (see table below).
- On success, redirects to the login page.

### cart.html – Shopping Cart
- Redirects to login if the user is not authenticated.
- Displays all cart items in a table with cover image, title, author, price, quantity, subtotal.
- Inline quantity editing with automatic subtotal / total recalculation.
- Remove individual items or clear the whole cart.
- Mock checkout button.

---

## Session Management

The logged-in user is stored in `localStorage` under the key `bookstore_user`:

```json
{ "id": 1, "username": "alice", "email": "alice@example.com" }
```

The password is **never** stored client-side.

---

## Validation Rules

| Field             | Rule                                                      |
|-------------------|-----------------------------------------------------------|
| username          | 3–50 characters, letters / digits / underscore only       |
| email             | Valid email format                                        |
| password          | Minimum 6 characters                                      |
| confirm password  | Must match the password field                             |
| quantity          | Integer ≥ 1                                               |

Validation runs on **both** the client (instant feedback) and the server
(defence in depth).

---

## How to Run

1. Ensure the backend is running at `http://localhost:8080/bookstore`.
2. Open `frontend/index.html` directly in a browser **or** serve the folder
   with any static file server, e.g.:

   ```bash
   # Python 3
   cd frontend
   python -m http.server 3000
   # then visit http://localhost:3000
   ```

> If you open the file directly via `file://`, most modern browsers allow
> `fetch()` calls to `localhost` without extra configuration.
> If you see CORS errors, use a local HTTP server instead.

---

## API Base URL

Defined in `js/auth.js`:

```js
const API_BASE = 'http://localhost:8080/bookstore/api';
```

Change this constant if your Tomcat runs on a different port or context path.

---

## Responsiveness

The layout adapts to all screen sizes:

- **Desktop (>768 px)** – multi-column book grid, full navbar.
- **Tablet (≤768 px)** – narrower grid, stacked cart table rows.
- **Mobile (≤480 px)** – single/double column grid, compact padding.
