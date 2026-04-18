package com.bookstore.model;

/**
 * Represents one line in a user's shopping cart,
 * optionally carrying the joined {@link Book} details.
 */
public class CartItem {

    private int  id;
    private int  userId;
    private int  bookId;
    private int  quantity;
    private Book book; // populated when fetching the cart

    public CartItem() {}

    // ---- Getters ----

    public int  getId()       { return id; }
    public int  getUserId()   { return userId; }
    public int  getBookId()   { return bookId; }
    public int  getQuantity() { return quantity; }
    public Book getBook()     { return book; }

    // ---- Setters ----

    public void setId(int id)            { this.id       = id; }
    public void setUserId(int userId)    { this.userId   = userId; }
    public void setBookId(int bookId)    { this.bookId   = bookId; }
    public void setQuantity(int qty)     { this.quantity = qty; }
    public void setBook(Book book)       { this.book     = book; }
}
