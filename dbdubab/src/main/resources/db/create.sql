-- Database 생성 
CREATE DATABASE IF NOT EXISTS db_project
	CHARACTER SET utf8
    COLLATE utf8_general_ci;

-- 1. pharmacy 테이블
CREATE TABLE pharmacy (
    pharmacy_id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address TEXT NOT NULL,
    phone VARCHAR(30),
    zip_code VARCHAR(10),
    longitude FLOAT,
    latitude FLOAT,
    is_operating BOOLEAN DEFAULT TRUE
);

CREATE INDEX idx_pharmacy_address ON pharmacy (address(255));
CREATE INDEX idx_pharmacy_operating ON pharmacy (is_operating);

-- 2. open_hours 테이블
CREATE TABLE open_hours (
    id INT AUTO_INCREMENT PRIMARY KEY,
    pharmacy_id VARCHAR(20),
    day_of_week VARCHAR(10),
    start_time TIME,
    end_time TIME,
    FOREIGN KEY (pharmacy_id) REFERENCES pharmacy(pharmacy_id)
);

-- 3. holiday_schedule 테이블
CREATE TABLE holiday_schedule (
    id INT AUTO_INCREMENT PRIMARY KEY,
    pharmacy_id VARCHAR(20),
    is_open_sunday BOOLEAN,
    is_open_holiday BOOLEAN,
    FOREIGN KEY (pharmacy_id) REFERENCES pharmacy(pharmacy_id)
);

-- 4. emergency_store 테이블
CREATE TABLE emergency_store (
    store_id VARCHAR(50) PRIMARY KEY,
    store_name VARCHAR(100),
    address TEXT,
    phone VARCHAR(30),
    longitude FLOAT,
    latitude FLOAT
);

-- 5. active_pharmacy 뷰
CREATE VIEW active_pharmacy AS
SELECT * FROM pharmacy
WHERE is_operating = TRUE;