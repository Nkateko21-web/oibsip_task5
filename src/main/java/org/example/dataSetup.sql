CREATE TABLE books (
book_id INT PRIMARY KEY ,
title VARCHAR(255),
author VARCHAR(255),
quantity INT
);

CREATE TABLE members (
member_id INT PRIMARY KEY,
name VARCHAR(255),
email VARCHAR(255)
);

CREATE TABLE transactions (
transaction_id INT PRIMARY KEY,
book_id INT,
member_id INT,
issue_date DATE,
return_date DATE,
fine_amount DOUBLE,
FOREIGN KEY (book_id) REFERENCES books(book_id),
FOREIGN KEY member_id REFERENCES members(member_id)
);

