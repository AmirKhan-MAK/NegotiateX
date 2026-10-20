CREATE TABLE categories (
    id        VARCHAR(36)  PRIMARY KEY,
    name      VARCHAR(100) UNIQUE NOT NULL,
    slug      VARCHAR(100) UNIQUE NOT NULL,
    parent_id VARCHAR(36)  NULL,
    FOREIGN KEY (parent_id) REFERENCES categories(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
