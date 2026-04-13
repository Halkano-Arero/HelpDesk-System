CREATE DATABASE IF NOT EXISTS helpdesk_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE helpdesk_db;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    category VARCHAR(100) DEFAULT 'Other',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS departments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS statuses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS tickets (
    id INT AUTO_INCREMENT PRIMARY KEY,
    helpdesk_id VARCHAR(50) UNIQUE,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL,
    department VARCHAR(100) NOT NULL,
    category VARCHAR(100) NOT NULL,
    subject VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    priority VARCHAR(20) NOT NULL,
    assigned_to VARCHAR(150),
    created_by VARCHAR(150) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'New',
    rating INT NOT NULL DEFAULT 0,
    agent_response TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT IGNORE INTO departments (name) VALUES
('IT Support'),
('HR Department'),
('Procurement'),
('Finance'),
('Other');

INSERT IGNORE INTO categories (name) VALUES
('Hardware'),
('Software'),
('Network'),
('Access'),
('Other');

INSERT IGNORE INTO statuses (name) VALUES
('New'),
('Open'),
('Answered'),
('Solved'),
('Closed');
