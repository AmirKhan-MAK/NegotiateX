CREATE TABLE user_behavior_events (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    user_id VARCHAR(36) NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    product_id VARCHAR(36),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES products(id),
    INDEX idx_behavior_user (user_id),
    INDEX idx_behavior_type (event_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
