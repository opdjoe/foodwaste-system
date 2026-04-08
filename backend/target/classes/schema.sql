-- ============================================================
-- Food Waste & Inventory Analytics System
-- MySQL Database Schema + Seed Data
-- ============================================================

CREATE DATABASE IF NOT EXISTS foodwaste_db
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE foodwaste_db;

-- ── Users ─────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS `user` (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    email      VARCHAR(100),
    role       ENUM('STAFF','MANAGER','ADMIN') NOT NULL DEFAULT 'STAFF',
    INDEX idx_user_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── Inventory ─────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS inventory (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(50)    NOT NULL,
    unit         VARCHAR(50)    NOT NULL,
    current_qty  DOUBLE         NOT NULL DEFAULT 0,
    expiry_date  DATE,
    INDEX idx_inventory_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── Alert Thresholds ──────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS alert_threshold (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id              BIGINT  NOT NULL UNIQUE,
    min_threshold        DOUBLE  NOT NULL,
    max_threshold        DOUBLE,
    notification_enabled TINYINT(1) NOT NULL DEFAULT 1,
    CONSTRAINT fk_alert_item FOREIGN KEY (item_id) REFERENCES inventory(id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── Waste Logs ────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS waste_logs (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id   BIGINT       NOT NULL,
    user_id   BIGINT       NOT NULL,
    weight    DOUBLE       NOT NULL,
    reason    VARCHAR(80),
    timestamp DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_waste_item      (item_id),
    INDEX idx_waste_user      (user_id),
    INDEX idx_waste_timestamp (timestamp),
    CONSTRAINT fk_waste_item FOREIGN KEY (item_id) REFERENCES inventory(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_waste_user FOREIGN KEY (user_id) REFERENCES `user`(id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── Seed Data ─────────────────────────────────────────────────────────────────
-- Passwords: BCrypt of "password123"
INSERT IGNORE INTO `user` (username, password, email, role) VALUES
('admin',   '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'admin@foodtrace.com',   'ADMIN'),
('manager', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'manager@foodtrace.com', 'MANAGER'),
('staff01', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'staff01@foodtrace.com', 'STAFF'),
('staff02', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'staff02@foodtrace.com', 'STAFF');

INSERT IGNORE INTO inventory (id, name, unit, current_qty, expiry_date) VALUES
(1, 'Chicken Breast',  'kg',     45.0,  DATE_ADD(CURDATE(), INTERVAL 5  DAY)),
(2, 'Basmati Rice',    'kg',    120.0,  DATE_ADD(CURDATE(), INTERVAL 90 DAY)),
(3, 'Whole Milk',      'L',      30.0,  DATE_ADD(CURDATE(), INTERVAL 3  DAY)),
(4, 'Bread Loaves',    'pieces', 24.0,  DATE_ADD(CURDATE(), INTERVAL 2  DAY)),
(5, 'Salad Greens',    'kg',      8.5,  DATE_ADD(CURDATE(), INTERVAL 1  DAY)),
(6, 'Tomato Sauce',    'L',      60.0,  DATE_ADD(CURDATE(), INTERVAL 180 DAY)),
(7, 'Cheddar Cheese',  'kg',     12.0,  DATE_ADD(CURDATE(), INTERVAL 14 DAY)),
(8, 'Cooking Oil',     'L',      25.0,  NULL),
(9, 'Ground Beef',     'kg',     18.0,  DATE_ADD(CURDATE(), INTERVAL 4  DAY)),
(10,'Orange Juice',    'L',      15.0,  DATE_ADD(CURDATE(), INTERVAL 7  DAY));

INSERT IGNORE INTO alert_threshold (item_id, min_threshold, max_threshold, notification_enabled) VALUES
(1,  20.0, 100.0, 1),
(2,  50.0, 200.0, 1),
(3,  20.0,  80.0, 1),
(4,  10.0,  50.0, 1),
(5,  10.0,  30.0, 1),
(7,   5.0,  30.0, 1),
(9,  15.0,  50.0, 1);

-- Sample waste logs (last 30 days)
INSERT IGNORE INTO waste_logs (item_id, user_id, weight, reason, timestamp) VALUES
(1, 3, 2.5, 'Expired',          DATE_SUB(NOW(), INTERVAL 1  DAY)),
(3, 3, 5.0, 'Overproduced',     DATE_SUB(NOW(), INTERVAL 2  DAY)),
(4, 4, 3.0, 'Stale',            DATE_SUB(NOW(), INTERVAL 3  DAY)),
(5, 3, 1.5, 'Wilted',           DATE_SUB(NOW(), INTERVAL 3  DAY)),
(1, 2, 3.0, 'Spoiled',          DATE_SUB(NOW(), INTERVAL 5  DAY)),
(2, 3, 4.0, 'Overcooked',       DATE_SUB(NOW(), INTERVAL 7  DAY)),
(9, 4, 2.0, 'Freezer burn',     DATE_SUB(NOW(), INTERVAL 8  DAY)),
(7, 3, 1.0, 'Mold',             DATE_SUB(NOW(), INTERVAL 10 DAY)),
(3, 4, 8.0, 'Order error',      DATE_SUB(NOW(), INTERVAL 12 DAY)),
(1, 3, 1.5, 'Dropped',          DATE_SUB(NOW(), INTERVAL 14 DAY)),
(5, 4, 2.5, 'Past date',        DATE_SUB(NOW(), INTERVAL 15 DAY)),
(4, 3, 4.0, 'Excess batch',     DATE_SUB(NOW(), INTERVAL 18 DAY)),
(10,3, 3.0, 'Expired',          DATE_SUB(NOW(), INTERVAL 20 DAY)),
(9, 2, 5.0, 'Storage issue',    DATE_SUB(NOW(), INTERVAL 22 DAY)),
(2, 4, 6.0, 'Menu change',      DATE_SUB(NOW(), INTERVAL 25 DAY));
