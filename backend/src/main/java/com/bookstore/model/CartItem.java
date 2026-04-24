package com.bookstore.model;
public class CartItem {
    public int id, userId, bookId, quantity; public Book book;
    public CartItem() {}
    public int getId() { return id; } public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; } public void setUserId(int u) { this.userId = u; }
    public int getBookId() { return bookId; } public void setBookId(int b) { this.bookId = b; }
    public int getQuantity() { return quantity; } public void setQuantity(int q) { this.quantity = q; }
    public Book getBook() { return book; } public void setBook(Book b) { this.book = b; }
}
