package com.bookstore.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

/**
 * Utility class for password hashing and verification using SHA-256.
 */
public class PasswordUtil {

    private PasswordUtil() {}

    /**
     * Returns the SHA-256 hex digest of the given plain-text password.
     *
     * @param password plain-text password
     * @return 64-character lowercase hex string
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(64);
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Checks whether {@code plainPassword} matches {@code storedHash}.
     *
     * @param plainPassword  the candidate password (plain text)
     * @param storedHash     the persisted SHA-256 hex digest
     * @return {@code true} if they match
     */
    public static boolean verifyPassword(String plainPassword, String storedHash) {
        return hashPassword(plainPassword).equals(storedHash);
    }
}
