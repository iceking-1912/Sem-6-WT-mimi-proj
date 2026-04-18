package com.bookstore.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void defaultConstructorInitializesFields() {
        User user = new User();
        assertEquals(0, user.getId());
        assertNull(user.getUsername());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
    }

    @Test
    void fullConstructorSetsAllFields() {
        User user = new User(42, "alice", "alice@example.com", "s3cret");
        assertEquals(42,                   user.getId());
        assertEquals("alice",              user.getUsername());
        assertEquals("alice@example.com",  user.getEmail());
        assertEquals("s3cret",             user.getPassword());
    }

    @Test
    void settersUpdateFields() {
        User user = new User();
        user.setId(7);
        user.setUsername("bob");
        user.setEmail("bob@example.com");
        user.setPassword("pass123");

        assertEquals(7,                 user.getId());
        assertEquals("bob",             user.getUsername());
        assertEquals("bob@example.com", user.getEmail());
        assertEquals("pass123",         user.getPassword());
    }

    @Test
    void passwordCanBeNulledOut() {
        User user = new User(1, "carol", "carol@mail.com", "secret");
        user.setPassword(null);
        assertNull(user.getPassword());
    }
}
