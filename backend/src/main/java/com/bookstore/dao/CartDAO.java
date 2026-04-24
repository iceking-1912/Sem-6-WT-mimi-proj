package com.bookstore.dao;
import com.bookstore.model.*; import com.bookstore.util.*; import java.sql.*; import java.util.*;
public class CartDAO {
    public List<CartItem> getCartByUserId(int u) throws SQLException {
        List<CartItem> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT c.*, b.title, b.author, b.price, b.image_url FROM cart c JOIN books b ON c.book_id = b.id WHERE c.user_id = ?")) {
            ps.setInt(1, u);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CartItem i = new CartItem(); i.setId(rs.getInt("id")); i.setUserId(u); i.setBookId(rs.getInt("book_id")); i.setQuantity(rs.getInt("quantity"));
                    Book b = new Book(); b.setId(i.getBookId()); b.setTitle(rs.getString("title")); b.setAuthor(rs.getString("author")); b.setPrice(rs.getDouble("price")); b.setImageUrl(rs.getString("image_url"));
                    i.setBook(b); list.add(i);
                }
            }
        }
        return list;
    }
    public CartItem addToCart(int u, int b, int q) throws SQLException {
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement("INSERT INTO cart (user_id, book_id, quantity) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE quantity = quantity + ?", Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, u); ps.setInt(2, b); ps.setInt(3, q); ps.setInt(4, q); ps.executeUpdate();
            CartItem item = new CartItem(); item.setUserId(u); item.setBookId(b); item.setQuantity(q); return item;
        }
    }
    public CartItem updateQuantity(int id, int q) throws SQLException {
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement("UPDATE cart SET quantity = ? WHERE id = ?")) {
            ps.setInt(1, q); ps.setInt(2, id); ps.executeUpdate();
            CartItem i = new CartItem(); i.setId(id); i.setQuantity(q); return i;
        }
    }
    public boolean removeFromCart(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM cart WHERE id = ?")) {
            ps.setInt(1, id); return ps.executeUpdate() > 0;
        }
    }
}
