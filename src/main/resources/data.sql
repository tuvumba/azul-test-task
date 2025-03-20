DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS book_author;
DROP TABLE IF EXISTS book_genre;
DROP TABLE IF EXISTS book;
DROP TABLE IF EXISTS author;
DROP TABLE IF EXISTS genre;

CREATE TABLE users (
                       username VARCHAR(255) PRIMARY KEY,
                       first_name VARCHAR(255),
                       last_name VARCHAR(255),
                       email VARCHAR(255),
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(255) NOT NULL CHECK (role IN ('ADMIN', 'USER'))
);

CREATE TABLE genre (
                       name VARCHAR(255) PRIMARY KEY
);

CREATE TABLE author (
                        id SERIAL PRIMARY KEY,
                        display_name VARCHAR(255) NOT NULL,
                        description VARCHAR(255)

);

CREATE TABLE book (
                      id BIGINT PRIMARY KEY,
                      name VARCHAR(255) NOT NULL,
                      price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
                      quantity INT NOT NULL CHECK (quantity >= 0)
);

CREATE TABLE book_genre (
                            book_id BIGINT NOT NULL,
                            genre_name VARCHAR(255) NOT NULL,
                            PRIMARY KEY (book_id, genre_name),
                            FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE,
                            FOREIGN KEY (genre_name) REFERENCES genre(name) ON DELETE CASCADE
);

CREATE TABLE book_author (
                             book_id BIGINT NOT NULL,
                             author_id BIGINT NOT NULL,
                             PRIMARY KEY (book_id, author_id),
                             FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE,
                             FOREIGN KEY (author_id) REFERENCES author(id) ON DELETE CASCADE
);

CREATE INDEX idx_book_author_author_id ON book_author(author_id);
CREATE INDEX idx_book_genre_genre_name ON book_genre(genre_name);
CREATE INDEX idx_book_quantity ON book(quantity);


INSERT INTO users (username, first_name, last_name, email, password, role)
VALUES ('admin', 'Admin', 'User', 'admin@example.com', 'admin', 'ADMIN');

INSERT INTO users (username, first_name, last_name, email, password, role)
VALUES ('user', 'General', 'User', 'user@example.com', 'user', 'USER');


INSERT INTO genre (name) VALUES
                             ('Science Fiction'),
                             ('Fantasy'),
                             ('Thriller'),
                             ('Mystery'),
                             ('Historical'),
                             ('Romance'),
                             ('Non-Fiction'),
                             ('Programming'),
                             ('Self-Help');

INSERT INTO author (display_name, description) VALUES
                                                   ('Robert C. Martin', 'Software engineer and author of Clean Code.'),
                                                   ('Stephen King', 'Prolific writer known for horror and suspense novels.'),
                                                   ('Agatha Christie', 'Best-selling mystery novelist, creator of Poirot.'),
                                                   ('Sun Tzu', 'Ancient Chinese general, author of The Art of War.'),
                                                   ('George Orwell', 'English novelist famous for 1984 and Animal Farm.'),
                                                   ('Haruki Murakami', 'Japanese author known for surreal and magical realism.'),
                                                   ('F. Scott Fitzgerald', 'American writer of The Great Gatsby and Jazz Age tales.'),
                                                   ('Dale Carnegie', 'Self-improvement guru, wrote How to Win Friends and Influence People.');


INSERT INTO book (id, name, price, quantity) VALUES
                                             (9780136083238,'Clean Code', 34.99, 20),
                                             (9780385121675,'The Shining', 18.99, 15),
                                             (9780008249670,'Murder on the Orient Express', 14.99, 20),
                                             (9781599869773,'The Art of War', 9.99, 35),
                                             (9788073098087,'1984', 12.50, 50),
                                             (9780099448822,'Norwegian Wood', 14.25, 30),
                                             (9780743273565,'The Great Gatsby', 10.99, 40),
                                             (9780671027032,'How to Win Friends & Influence People', 19.99, 25);

INSERT INTO book_genre (book_id, genre_name) VALUES
                                               (9780136083238, 'Programming'), -- 'Clean Code' -> Programming
                                               (9780385121675, 'Thriller'), -- 'The Shining' -> Thriller
                                               (9780008249670, 'Mystery'), -- 'Murder on the Orient Express' -> Mystery
                                               (9781599869773, 'Historical'), -- 'The Art of War' -> Historical
                                               (9788073098087, 'Science Fiction'), -- '1984' -> Science Fiction
                                               (9780099448822, 'Romance'), -- 'Norwegian Wood' -> Romance
                                               (9780743273565, 'Romance'), -- 'The Great Gatsby' -> Romance
                                               (9780671027032, 'Self-Help'),
                                               (9780671027032, 'Mystery'); -- 'How to Win Friends & Influence People' -> Self-Help and mystery :))

INSERT INTO book_author (book_id, author_id) VALUES
                                                 (9780136083238, 1),  -- 'Clean Code' -> Robert C. Martin
                                                 (9780136083238, 2),  -- 'Clean Code' (erroneously) -> Stephen King
                                                 (9780385121675, 2),  -- 'The Shining' -> Stephen King
                                                 (9780008249670, 3),  -- 'Murder on the Orient Express' -> Agatha Christie
                                                 (9781599869773, 4),  -- 'The Art of War' -> Sun Tzu
                                                 (9788073098087, 5),  -- '1984' -> George Orwell
                                                 (9780743273565, 6),  -- 'Norwegian Wood' -> Haruki Murakami
                                                 (9780743273565, 7),  -- 'The Great Gatsby' -> F. Scott Fitzgerald
                                                 (9780671027032, 8);  -- 'How to Win Friends and Influence People' -> Dale Carnegie
