package com.bookstore.util;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
public class PasswordUtil {
    public static String hashPassword(String p) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] h = md.digest(p.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : h) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) { throw new RuntimeException(e); }
    }
    public static boolean verifyPassword(String p, String h) { return hashPassword(p).equals(h); }
}
