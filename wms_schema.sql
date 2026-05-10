-- ============================================================
-- Warehouse Management System — Database Schema (MySQL)
-- Optional: This project uses CSV file persistence by default.
-- These scripts are provided for teams wishing to migrate to MySQL.
-- ============================================================

CREATE DATABASE IF NOT EXISTS wms_db;
USE wms_db;

-- Categories
CREATE TABLE categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description TEXT
);

-- Suppliers
CREATE TABLE suppliers (
    supplier_id    INT AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(150) NOT NULL,
    contact_person VARCHAR(100),
    email          VARCHAR(100),
    phone          VARCHAR(30)
);

-- Products
CREATE TABLE products (
    product_id    INT AUTO_INCREMENT PRIMARY KEY,
    sku           VARCHAR(50)  NOT NULL UNIQUE,
    name          VARCHAR(200) NOT NULL,
    current_stock INT          NOT NULL DEFAULT 0,
    min_threshold INT          NOT NULL DEFAULT 5,
    unit_price    DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    category_id   INT,
    supplier_id   INT,
    FOREIGN KEY (category_id) REFERENCES categories(category_id),
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id)
);

-- Transactions (audit trail)
CREATE TABLE transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id     INT          NOT NULL,
    type           ENUM('STOCK_IN','STOCK_OUT') NOT NULL,
    quantity       INT          NOT NULL,
    performed_by   VARCHAR(100),
    notes          TEXT,
    timestamp      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- Users
CREATE TABLE users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    full_name     VARCHAR(150),
    role          ENUM('MANAGER','WORKER') NOT NULL DEFAULT 'WORKER'
);

-- Sample Data
INSERT INTO categories (name, description) VALUES
    ('Electronics',    'Electronic components and devices'),
    ('Office Supplies','Stationery and office materials'),
    ('Furniture',      'Office and warehouse furniture');

INSERT INTO suppliers (name, contact_person, email, phone) VALUES
    ('TechCorp', 'Ali Raza',   'ali@techcorp.com',  '+92-300-1111111'),
    ('SupplyCo', 'Sara Khan',  'sara@supplyco.com', '+92-300-2222222');

INSERT INTO products (sku, name, current_stock, min_threshold, unit_price, category_id, supplier_id) VALUES
    ('SKU001', 'USB Cables',   50, 10, 5.99,  1, 1),
    ('SKU002', 'A4 Paper Ream', 8, 15, 3.49,  2, 2),
    ('SKU003', 'Office Chair',  3,  5, 89.99, 3, 2),
    ('SKU004', 'HDMI Cables',   0,  5, 12.00, 1, 1);
