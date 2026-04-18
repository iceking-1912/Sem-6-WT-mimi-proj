package com.bookstore.dao;

import com.bookstore.model.User;
import com.bookstore.util.DBConnection;
import com.bookstore.util.PasswordUtil;

import java.sql.*;

/**
 * Data-access object for the {@code users} table.
 */
public class UserDAO {

    /**
     * Persists a new user.  The password is hashed before storage.
     * The returned {@link User} has the generated id set and the password
     * field cleared.
     *
     * @param user user with plain-text password
     * @return saved user (no password)
     * @throws SQLException on DB errors, including duplicate username/email
     */
    public User register(User user) throws SQLException {
        String sql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, PasswordUtil.hashPassword(user.getPassword()));
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setId(keys.getInt(1));
                }
            }
        }
        user.setPassword(null); // never expose hashed password
        return user;
    }

    /**
     * Authenticates a user by username + password.
     *
     * @return the matching {@link User} (no password), or {@code null} if credentials are invalid
     */
    public User login(String username, String password) throws SQLException {
        String sql = "SELECT id, username, email, password FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    if (PasswordUtil.verifyPassword(password, storedHash)) {
                        User u = new User();
                        u.setId(rs.getInt("id"));
                        u.setUsername(rs.getString("username"));
                        u.setEmail(rs.getString("email"));
                        return u;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns {@code true} if the given username is already taken.
     */
    public boolean usernameExists(String username) throws SQLException {
        return countByUsername(username) > 0;
    }

    /**
     * Returns {@code true} if the given email address is already registered.
     */
    public boolean emailExists(String email) throws SQLException {
        return countByEmail(email) > 0;
    }

    private int countByUsername(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    private int countByEmail(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }
}
