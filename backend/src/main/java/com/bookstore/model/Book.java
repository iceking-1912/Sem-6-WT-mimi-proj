package com.bookstore.model;
public class Book {
    public int id; public String title, author, imageUrl, description, category; public double price;
    public Book() {}
    public Book(int id, String t, String a, double p, String img, String d, String c) {
        this.id=id; this.title=t; this.author=a; this.price=p; this.imageUrl=img; this.description=d; this.category=c;
    }
    public int getId() { return id; } public void setId(int id) { this.id = id; }
    public String getTitle() { return title; } public void setTitle(String t) { this.title = t; }
    public String getAuthor() { return author; } public void setAuthor(String a) { this.author = a; }
    public double getPrice() { return price; } public void setPrice(double p) { this.price = p; }
    public String getImageUrl() { return imageUrl; } public void setImageUrl(String i) { this.imageUrl = i; }
    public String getDescription() { return description; } public void setDescription(String d) { this.description = d; }
    public String getCategory() { return category; } public void setCategory(String c) { this.category = c; }
}
