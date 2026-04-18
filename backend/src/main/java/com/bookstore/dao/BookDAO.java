package com.bookstore.dao;

import com.bookstore.model.Book;
import com.bookstore.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data-access object for the {@code books} table.
 */
public class BookDAO {

    /**
     * Returns all books in the catalog, ordered by title.
     */
    public List<Book> getAllBooks() throws SQLException {
        String sql = "SELECT * FROM books ORDER BY title";
        List<Book> books = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                books.add(map(rs));
            }
        }
        return books;
    }

    /**
     * Returns books whose title or author contains {@code keyword}
     * (case-insensitive).
     *
     * @param keyword non-null search term
     */
    public List<Book> searchBooks(String keyword) throws SQLException {
        String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? ORDER BY title";
        List<Book> books = new ArrayList<>();
        String like = "%" + keyword + "%";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, like);
            stmt.setString(2, like);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(map(rs));
                }
            }
        }
        return books;
    }

    /**
     * Returns the book with the given primary key, or {@code null} if absent.
     */
    public Book getBookById(int id) throws SQLException {
        String sql = "SELECT * FROM books WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    // Maps the current ResultSet row to a Book object.
    private Book map(ResultSet rs) throws SQLException {
        return new Book(
            rs.getInt("id"),
            rs.getString("title"),
            rs.getString("author"),
            rs.getDouble("price"),
            rs.getString("image_url"),
            rs.getString("description"),
            rs.getString("category")
        );
    }
}
