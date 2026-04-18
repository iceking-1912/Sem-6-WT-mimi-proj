package com.bookstore.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Utility class that provides JDBC connections.
 * <p>
 * Connection details are read from {@code db.properties} on the classpath.
 * Copy {@code db.properties.example} to {@code db.properties} and fill in
 * your credentials before building.
 */
public class DBConnection {

    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        Properties props = new Properties();
        try (InputStream in = DBConnection.class
                .getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new ExceptionInInitializerError(
                    "db.properties not found on classpath. " +
                    "Copy db.properties.example to db.properties and fill in your credentials.");
            }
            props.load(in);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
        URL      = props.getProperty("db.url");
        USER     = props.getProperty("db.username");
        PASSWORD = props.getProperty("db.password");
    }

    private DBConnection() {}

    /**
     * Opens and returns a new JDBC connection.
     *
     * @return a new {@link Connection}
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
