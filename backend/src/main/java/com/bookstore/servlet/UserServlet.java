package com.bookstore.servlet;

import com.bookstore.dao.UserDAO;
import com.bookstore.model.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.sql.SQLException;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Handles user registration and login.
 * <p>
 * POST /api/users/register – create a new account<br>
 * POST /api/users/login    – authenticate an existing account
 */
@WebServlet("/api/users/*")
public class UserServlet extends HttpServlet {

    private static final Pattern USERNAME_RE =
            Pattern.compile("^[A-Za-z0-9_]{3,50}$");
    private static final Pattern EMAIL_RE =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final UserDAO userDAO = new UserDAO();
    private final Gson    gson    = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String path = req.getPathInfo(); // "/register" or "/login"

        try {
            JsonObject body = parseBody(req);

            if ("/register".equals(path)) {
                handleRegister(body, resp, out);
            } else if ("/login".equals(path)) {
                handleLogin(body, resp, out);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gson.toJson(Map.of("error", "Endpoint not found")));
            }
        } catch (JsonSyntaxException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.toJson(Map.of("error", "Invalid JSON body")));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(Map.of("error", "Database error: " + e.getMessage())));
        } finally {
            out.flush();
        }
    }

    // ---- handlers -------------------------------------------------------

    private void handleRegister(JsonObject body, HttpServletResponse resp, PrintWriter out)
            throws IOException, SQLException {

        String username = str(body, "username");
        String email    = str(body, "email");
        String password = str(body, "password");

        // --- field validation ---
        if (username == null || username.isBlank()) {
            send(resp, out, 400, "Username is required");
            return;
        }
        if (!USERNAME_RE.matcher(username).matches()) {
            send(resp, out, 400,
                 "Username must be 3–50 characters (letters, digits, underscore only)");
            return;
        }
        if (email == null || email.isBlank()) {
            send(resp, out, 400, "Email is required");
            return;
        }
        if (!EMAIL_RE.matcher(email).matches()) {
            send(resp, out, 400, "Invalid email address");
            return;
        }
        if (password == null || password.length() < 6) {
            send(resp, out, 400, "Password must be at least 6 characters");
            return;
        }

        // --- uniqueness checks ---
        if (userDAO.usernameExists(username)) {
            send(resp, out, 409, "Username is already taken");
            return;
        }
        if (userDAO.emailExists(email)) {
            send(resp, out, 409, "Email address is already registered");
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);

        User saved = userDAO.register(user);
        resp.setStatus(HttpServletResponse.SC_CREATED);
        out.print(gson.toJson(saved));
    }

    private void handleLogin(JsonObject body, HttpServletResponse resp, PrintWriter out)
            throws IOException, SQLException {

        String username = str(body, "username");
        String password = str(body, "password");

        if (username == null || username.isBlank()) {
            send(resp, out, 400, "Username is required");
            return;
        }
        if (password == null || password.isBlank()) {
            send(resp, out, 400, "Password is required");
            return;
        }

        User user = userDAO.login(username, password);
        if (user == null) {
            send(resp, out, 401, "Invalid username or password");
            return;
        }
        out.print(gson.toJson(user));
    }

    // ---- helpers --------------------------------------------------------

    private JsonObject parseBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return gson.fromJson(sb.toString(), JsonObject.class);
    }

    /** Extracts a string field from a JSON object, returning null if absent. */
    private String str(JsonObject obj, String key) {
        if (obj != null && obj.has(key) && !obj.get(key).isJsonNull()) {
            return obj.get(key).getAsString();
        }
        return null;
    }

    /** Writes an error response with the given HTTP status. */
    private void send(HttpServletResponse resp, PrintWriter out, int status, String msg) {
        resp.setStatus(status);
        out.print(gson.toJson(Map.of("error", msg)));
    }
}
