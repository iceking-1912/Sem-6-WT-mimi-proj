package com.bookstore.dao;
import com.bookstore.model.*; import com.bookstore.util.*; import java.sql.*; import java.util.*;
public class BookDAO {
    public List<Book> getAllBooks() throws SQLException {
        List<Book> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection(); Statement s = c.createStatement(); ResultSet rs = s.executeQuery("SELECT * FROM books ORDER BY title")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }
    public List<Book> searchBooks(String k) throws SQLException {
        List<Book> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM books WHERE title LIKE ? OR author LIKE ?")) {
            ps.setString(1, "%"+k+"%"); ps.setString(2, "%"+k+"%");
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }
    private Book map(ResultSet rs) throws SQLException {
        return new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author"), rs.getDouble("price"), rs.getString("image_url"), rs.getString("description"), rs.getString("category"));
    }
}
