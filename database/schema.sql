-- Online Book Store – Database Schema
-- Run this file first to create the database and tables.

CREATE DATABASE IF NOT EXISTS bookstore
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE bookstore;

-- Books table
CREATE TABLE IF NOT EXISTS books (
    id          INT PRIMARY KEY AUTO_INCREMENT,
    title       VARCHAR(255)   NOT NULL,
    author      VARCHAR(255)   NOT NULL,
    price       DECIMAL(10,2)  NOT NULL,
    image_url   VARCHAR(500)   DEFAULT NULL,
    description TEXT           DEFAULT NULL,
    category    VARCHAR(100)   DEFAULT NULL,
    created_at  TIMESTAMP      DEFAULT CURRENT_TIMESTAMP
);

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id          INT PRIMARY KEY AUTO_INCREMENT,
    username    VARCHAR(50)    NOT NULL UNIQUE,
    email       VARCHAR(255)   NOT NULL UNIQUE,
    password    VARCHAR(64)    NOT NULL,        -- SHA-256 hex digest (64 chars)
    created_at  TIMESTAMP      DEFAULT CURRENT_TIMESTAMP
);

-- Cart table
CREATE TABLE IF NOT EXISTS cart (
    id          INT PRIMARY KEY AUTO_INCREMENT,
    user_id     INT            NOT NULL,
    book_id     INT            NOT NULL,
    quantity    INT            NOT NULL DEFAULT 1,
    added_at    TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES users(id)  ON DELETE CASCADE,
    CONSTRAINT fk_cart_book FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    CONSTRAINT uq_cart_user_book UNIQUE (user_id, book_id)
);
