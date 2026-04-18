package com.bookstore.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    @Test
    void defaultConstructorInitializesFields() {
        Book book = new Book();
        assertEquals(0, book.getId());
        assertNull(book.getTitle());
        assertNull(book.getAuthor());
        assertEquals(0.0, book.getPrice(), 1e-9);
        assertNull(book.getImageUrl());
        assertNull(book.getDescription());
        assertNull(book.getCategory());
    }

    @Test
    void fullConstructorSetsAllFields() {
        Book book = new Book(1, "Clean Code", "Robert Martin", 39.99,
                             "cover.jpg", "Best practices", "Technology");
        assertEquals(1,            book.getId());
        assertEquals("Clean Code", book.getTitle());
        assertEquals("Robert Martin", book.getAuthor());
        assertEquals(39.99, book.getPrice(), 1e-9);
        assertEquals("cover.jpg",  book.getImageUrl());
        assertEquals("Best practices", book.getDescription());
        assertEquals("Technology", book.getCategory());
    }

    @Test
    void settersUpdateFields() {
        Book book = new Book();
        book.setId(5);
        book.setTitle("1984");
        book.setAuthor("George Orwell");
        book.setPrice(10.99);
        book.setImageUrl("1984.jpg");
        book.setDescription("A dystopian novel");
        book.setCategory("Fiction");

        assertEquals(5,                book.getId());
        assertEquals("1984",           book.getTitle());
        assertEquals("George Orwell",  book.getAuthor());
        assertEquals(10.99,            book.getPrice(), 1e-9);
        assertEquals("1984.jpg",       book.getImageUrl());
        assertEquals("A dystopian novel", book.getDescription());
        assertEquals("Fiction",        book.getCategory());
    }

    @Test
    void priceAcceptsZero() {
        Book book = new Book();
        book.setPrice(0.0);
        assertEquals(0.0, book.getPrice(), 1e-9);
    }
}
