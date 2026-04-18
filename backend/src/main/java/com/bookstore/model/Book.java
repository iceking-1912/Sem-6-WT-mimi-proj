package com.bookstore.model;

/**
 * Represents a book in the store catalog.
 */
public class Book {

    private int    id;
    private String title;
    private String author;
    private double price;
    private String imageUrl;
    private String description;
    private String category;

    public Book() {}

    public Book(int id, String title, String author, double price,
                String imageUrl, String description, String category) {
        this.id          = id;
        this.title       = title;
        this.author      = author;
        this.price       = price;
        this.imageUrl    = imageUrl;
        this.description = description;
        this.category    = category;
    }

    // ---- Getters ----

    public int    getId()          { return id; }
    public String getTitle()       { return title; }
    public String getAuthor()      { return author; }
    public double getPrice()       { return price; }
    public String getImageUrl()    { return imageUrl; }
    public String getDescription() { return description; }
    public String getCategory()    { return category; }

    // ---- Setters ----

    public void setId(int id)                  { this.id          = id; }
    public void setTitle(String title)         { this.title       = title; }
    public void setAuthor(String author)       { this.author      = author; }
    public void setPrice(double price)         { this.price       = price; }
    public void setImageUrl(String imageUrl)   { this.imageUrl    = imageUrl; }
    public void setDescription(String desc)    { this.description = desc; }
    public void setCategory(String category)   { this.category    = category; }
}
