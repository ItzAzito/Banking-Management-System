CREATE DATABASE bank_db;

USE bank_db;

CREATE TABLE accounts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    balance DOUBLE
);

CREATE TABLE transactions (
    tid INT PRIMARY KEY AUTO_INCREMENT,
    acc_id INT,
    type VARCHAR(20),
    amount DOUBLE,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);