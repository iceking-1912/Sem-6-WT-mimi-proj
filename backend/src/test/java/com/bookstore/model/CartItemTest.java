package com.bookstore.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CartItemTest {

    @Test
    void defaultConstructorInitializesFields() {
        CartItem item = new CartItem();
        assertEquals(0, item.getId());
        assertEquals(0, item.getUserId());
        assertEquals(0, item.getBookId());
        assertEquals(0, item.getQuantity());
        assertNull(item.getBook());
    }

    @Test
    void settersUpdateFields() {
        CartItem item = new CartItem();
        item.setId(10);
        item.setUserId(3);
        item.setBookId(7);
        item.setQuantity(2);

        Book book = new Book();
        book.setTitle("The Hobbit");
        book.setPrice(14.99);
        item.setBook(book);

        assertEquals(10, item.getId());
        assertEquals(3,  item.getUserId());
        assertEquals(7,  item.getBookId());
        assertEquals(2,  item.getQuantity());
        assertNotNull(item.getBook());
        assertEquals("The Hobbit", item.getBook().getTitle());
        assertEquals(14.99, item.getBook().getPrice(), 1e-9);
    }

    @Test
    void bookReferenceCanBeNulled() {
        CartItem item = new CartItem();
        item.setBook(null);
        assertNull(item.getBook());
    }
}
