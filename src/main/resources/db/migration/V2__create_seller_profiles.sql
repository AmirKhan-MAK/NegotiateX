CREATE TABLE seller_profiles (
    id           VARCHAR(36)   PRIMARY KEY,
    user_id      VARCHAR(36)   UNIQUE NOT NULL,
    shop_name    VARCHAR(200)  NOT NULL,
    description  TEXT,
    rating       DECIMAL(3,2)  DEFAULT 0.00,
    total_sales  INT           DEFAULT 0,
    created_at   TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
