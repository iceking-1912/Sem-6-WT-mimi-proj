-- Online Book Store – Sample Data
-- Run this file after schema.sql

USE bookstore;

INSERT INTO books (title, author, price, image_url, description, category) VALUES
('The Great Gatsby',        'F. Scott Fitzgerald', 9.99,  'https://covers.openlibrary.org/b/id/8432509-M.jpg',  'A story of decadence and excess.', 'Fiction'),
('To Kill a Mockingbird',   'Harper Lee',          12.99, 'https://covers.openlibrary.org/b/id/8810494-M.jpg',  'A classic of modern American literature.', 'Fiction'),
('1984',                    'George Orwell',       10.99, 'https://covers.openlibrary.org/b/id/8575708-M.jpg',  'A dystopian social science fiction.', 'Fiction'),
('Pride and Prejudice',     'Jane Austen',         8.99,  'https://covers.openlibrary.org/b/id/8739161-M.jpg',  'A romantic novel of manners.', 'Romance'),
('The Hobbit',              'J.R.R. Tolkien',      14.99, 'https://covers.openlibrary.org/b/id/6979861-M.jpg',  'A fantasy novel and prelude to LOTR.', 'Fantasy'),
('Harry Potter and the Philosopher''s Stone', 'J.K. Rowling', 15.99, 'https://covers.openlibrary.org/b/id/10527843-M.jpg', 'A young wizard''s journey begins.', 'Fantasy'),
('The Alchemist',           'Paulo Coelho',        11.99, 'https://covers.openlibrary.org/b/id/8371163-M.jpg',  'A philosophical novel about a shepherd.', 'Fiction'),
('Clean Code',              'Robert C. Martin',    39.99, 'https://covers.openlibrary.org/b/id/8832438-M.jpg',  'A handbook of agile software craftsmanship.', 'Technology'),
('Introduction to Algorithms', 'Thomas H. Cormen', 49.99, 'https://covers.openlibrary.org/b/id/8369174-M.jpg', 'Comprehensive guide to algorithms.', 'Technology'),
('Sapiens',                 'Yuval Noah Harari',   16.99, 'https://covers.openlibrary.org/b/id/9164332-M.jpg',  'A brief history of humankind.', 'History');
