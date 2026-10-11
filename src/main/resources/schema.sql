-- =============================================
-- Zesto Food Delivery Platform - Database Schema
-- Compatible with MySQL 8.0+ and H2 Database
-- =============================================

CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    role VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS categories (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    image VARCHAR(1000)
);

CREATE TABLE IF NOT EXISTS coupons (
    code VARCHAR(64) PRIMARY KEY,
    discount_percentage INT,
    max_discount INT,
    min_order INT,
    is_free_delivery BOOLEAN,
    description VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS riders (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    rating DOUBLE,
    vehicle VARCHAR(100),
    avatar VARCHAR(1000)
);

CREATE TABLE IF NOT EXISTS restaurants (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    image VARCHAR(1000),
    rating DOUBLE,
    rating_count VARCHAR(50),
    delivery_time VARCHAR(50),
    cost_for_two INT,
    location VARCHAR(255),
    is_pure_veg BOOLEAN,
    offer VARCHAR(255),
    is_open BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS restaurant_cuisines (
    restaurant_id VARCHAR(64) NOT NULL,
    cuisine VARCHAR(100) NOT NULL,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS menu_items (
    id VARCHAR(64) PRIMARY KEY,
    restaurant_id VARCHAR(64),
    name VARCHAR(255) NOT NULL,
    price INT NOT NULL,
    original_price INT,
    category VARCHAR(100),
    is_veg BOOLEAN,
    is_bestseller BOOLEAN,
    rating DOUBLE,
    rating_count INT,
    description VARCHAR(1000),
    image VARCHAR(1000),
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS orders (
    order_id VARCHAR(64) PRIMARY KEY,
    customer_name VARCHAR(255),
    customer_phone VARCHAR(50),
    delivery_address VARCHAR(500),
    restaurant_id VARCHAR(64),
    restaurant_name VARCHAR(255),
    item_total INT,
    taxes_and_fee INT,
    discount INT,
    grand_total INT,
    payment_method VARCHAR(100),
    payment_status VARCHAR(50),
    status VARCHAR(50),
    placed_at VARCHAR(100),
    eta_minutes INT,
    rider_id VARCHAR(64),
    rider_name VARCHAR(255),
    rider_phone VARCHAR(50),
    rider_rating DOUBLE,
    rider_vehicle VARCHAR(100),
    rider_avatar VARCHAR(1000)
);

CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id VARCHAR(64),
    item_id VARCHAR(64),
    name VARCHAR(255),
    price INT,
    quantity INT,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE
);
