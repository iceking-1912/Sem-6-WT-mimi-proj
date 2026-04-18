package com.bookstore.model;

/**
 * Represents a registered user.
 */
public class User {

    private int    id;
    private String username;
    private String email;
    private String password; // not included in API responses after registration

    public User() {}

    public User(int id, String username, String email, String password) {
        this.id       = id;
        this.username = username;
        this.email    = email;
        this.password = password;
    }

    // ---- Getters ----

    public int    getId()       { return id; }
    public String getUsername() { return username; }
    public String getEmail()    { return email; }
    public String getPassword() { return password; }

    // ---- Setters ----

    public void setId(int id)              { this.id       = id; }
    public void setUsername(String u)      { this.username = u; }
    public void setEmail(String email)     { this.email    = email; }
    public void setPassword(String pass)   { this.password = pass; }
}
