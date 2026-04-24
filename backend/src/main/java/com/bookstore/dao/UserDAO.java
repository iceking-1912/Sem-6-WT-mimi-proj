package com.bookstore.dao;
import com.bookstore.model.*; import com.bookstore.util.*; import java.sql.*;
public class UserDAO {
    public User register(User u) throws SQLException {
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement("INSERT INTO users (username, email, password) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getUsername()); ps.setString(2, u.getEmail()); ps.setString(3, PasswordUtil.hashPassword(u.getPassword()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) u.setId(rs.getInt(1)); }
        }
        u.setPassword(null); return u;
    }
    public User login(String u, String p) throws SQLException {
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM users WHERE username = ?")) {
            ps.setString(1, u);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && PasswordUtil.verifyPassword(p, rs.getString("password"))) {
                    User user = new User(); user.setId(rs.getInt("id")); user.setUsername(rs.getString("username")); user.setEmail(rs.getString("email")); return user;
                }
            }
        }
        return null;
    }
    public boolean usernameExists(String v) throws SQLException { return exists("username", v); }
    public boolean emailExists(String v) throws SQLException { return exists("email", v); }
    private boolean exists(String f, String v) throws SQLException {
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM users WHERE " + f + " = ?")) {
            ps.setString(1, v); try (ResultSet rs = ps.executeQuery()) { return rs.next() && rs.getInt(1) > 0; }
        }
    }
}
