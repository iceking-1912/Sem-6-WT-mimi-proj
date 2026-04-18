package com.bookstore.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

    @Test
    void hashReturnsSHA256HexDigest() {
        String hash = PasswordUtil.hashPassword("password123");
        assertNotNull(hash);
        assertEquals(64, hash.length(), "SHA-256 hex digest must be 64 characters");
        assertTrue(hash.matches("[0-9a-f]+"), "Digest must contain only hex characters");
    }

    @Test
    void hashIsDeterministic() {
        String h1 = PasswordUtil.hashPassword("myPassword");
        String h2 = PasswordUtil.hashPassword("myPassword");
        assertEquals(h1, h2, "Same input must always produce the same hash");
    }

    @Test
    void differentPasswordsProduceDifferentHashes() {
        assertNotEquals(
            PasswordUtil.hashPassword("password1"),
            PasswordUtil.hashPassword("password2")
        );
    }

    @Test
    void verifyPasswordReturnsTrueForCorrectPassword() {
        String password = "securePass!99";
        String hash     = PasswordUtil.hashPassword(password);
        assertTrue(PasswordUtil.verifyPassword(password, hash));
    }

    @Test
    void verifyPasswordReturnsFalseForWrongPassword() {
        String hash = PasswordUtil.hashPassword("correctPassword");
        assertFalse(PasswordUtil.verifyPassword("wrongPassword", hash));
    }

    @Test
    void verifyPasswordReturnsFalseForEmptyCandidate() {
        String hash = PasswordUtil.hashPassword("somePassword");
        assertFalse(PasswordUtil.verifyPassword("", hash));
    }

    @Test
    void emptyStringHasConsistentHash() {
        String h1 = PasswordUtil.hashPassword("");
        String h2 = PasswordUtil.hashPassword("");
        assertEquals(h1, h2);
        assertEquals(64, h1.length());
    }
}
