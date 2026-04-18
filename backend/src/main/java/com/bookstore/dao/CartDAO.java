package com.bookstore.dao;

import com.bookstore.model.Book;
import com.bookstore.model.CartItem;
import com.bookstore.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data-access object for the {@code cart} table.
 */
public class CartDAO {

    /**
     * Returns all cart items for the given user, with book details joined in.
     */
    public List<CartItem> getCartByUserId(int userId) throws SQLException {
        String sql =
            "SELECT c.id, c.user_id, c.book_id, c.quantity, " +
            "       b.title, b.author, b.price, b.image_url " +
            "FROM   cart c " +
            "JOIN   books b ON c.book_id = b.id " +
            "WHERE  c.user_id = ? " +
            "ORDER BY c.id";
        List<CartItem> items = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapWithBook(rs));
                }
            }
        }
        return items;
    }

    /**
     * Adds {@code quantity} units of {@code bookId} to the user's cart.
     * If the book is already present the quantity is incremented.
     *
     * @return the affected {@link CartItem}
     */
    public CartItem addToCart(int userId, int bookId, int quantity) throws SQLException {
        // Check if the book is already in the cart
        String check = "SELECT id, quantity FROM cart WHERE user_id = ? AND book_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(check)) {
            ps.setInt(1, userId);
            ps.setInt(2, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int cartId     = rs.getInt("id");
                    int newQty     = rs.getInt("quantity") + quantity;
                    return updateQuantity(cartId, newQty);
                }
            }
        }

        // Insert new row
        String insert = "INSERT INTO cart (user_id, book_id, quantity) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setInt(2, bookId);
            ps.setInt(3, quantity);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    CartItem item = new CartItem();
                    item.setId(keys.getInt(1));
                    item.setUserId(userId);
                    item.setBookId(bookId);
                    item.setQuantity(quantity);
                    return item;
                }
            }
        }
        return null;
    }

    /**
     * Updates the quantity of a cart item identified by {@code cartId}.
     */
    public CartItem updateQuantity(int cartId, int quantity) throws SQLException {
        String sql = "UPDATE cart SET quantity = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, cartId);
            ps.executeUpdate();
        }
        CartItem item = new CartItem();
        item.setId(cartId);
        item.setQuantity(quantity);
        return item;
    }

    /**
     * Removes the cart row with the given primary key.
     *
     * @return {@code true} if a row was deleted
     */
    public boolean removeFromCart(int cartId) throws SQLException {
        String sql = "DELETE FROM cart WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cartId);
            return ps.executeUpdate() > 0;
        }
    }

    // Maps a ResultSet row (with joined book columns) to a CartItem.
    private CartItem mapWithBook(ResultSet rs) throws SQLException {
        CartItem item = new CartItem();
        item.setId(rs.getInt("id"));
        item.setUserId(rs.getInt("user_id"));
        item.setBookId(rs.getInt("book_id"));
        item.setQuantity(rs.getInt("quantity"));

        Book book = new Book();
        book.setId(rs.getInt("book_id"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setPrice(rs.getDouble("price"));
        book.setImageUrl(rs.getString("image_url"));
        item.setBook(book);

        return item;
    }
}
