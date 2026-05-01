package com.bookstore.util;
import java.io.*;
import java.sql.*;
import java.util.Properties;
public class DBConnection {
    private static final String URL, USER, PASSWORD;
    static {
        Properties p = new Properties();
        try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) throw new RuntimeException("db.properties not found");
            p.load(in);
        } catch (IOException e) { throw new RuntimeException(e); }
        URL = p.getProperty("db.url");
        USER = p.getProperty("db.username");
        PASSWORD = p.getProperty("db.password");
    }
    private DBConnection() {}
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
