-- Create Products
CREATE TABLE products (
    id              VARCHAR(36)    PRIMARY KEY,
    seller_id       VARCHAR(36)    NOT NULL,
    category_id     VARCHAR(36),
    name            VARCHAR(255)   NOT NULL,
    description     TEXT,
    base_price      DECIMAL(12,2)  NOT NULL,
    floor_price     DECIMAL(12,2)  NOT NULL,     -- min negotiable price
    stock           INT            NOT NULL DEFAULT 0,
    negotiable      BOOLEAN        DEFAULT TRUE,
    is_near_expiry  BOOLEAN        DEFAULT FALSE,
    expiry_date     DATE           NULL,
    image_url       VARCHAR(500),
    is_active       BOOLEAN        DEFAULT TRUE,
    created_at      TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT chk_floor_lt_base CHECK (floor_price < base_price),
    CONSTRAINT chk_stock_positive CHECK (stock >= 0),

    FOREIGN KEY (seller_id)   REFERENCES users(id),
    FOREIGN KEY (category_id) REFERENCES categories(id),

    INDEX idx_products_seller   (seller_id),
    INDEX idx_products_category (category_id),
    INDEX idx_products_expiry   (is_near_expiry, expiry_date),
    INDEX idx_products_active   (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create Bulk Tiers
CREATE TABLE bulk_tiers (
    id          VARCHAR(36)   PRIMARY KEY,
    product_id  VARCHAR(36)   NOT NULL,
    min_qty     INT           NOT NULL,
    max_qty     INT           NULL,                -- NULL = unlimited
    price       DECIMAL(12,2) NOT NULL,

    CONSTRAINT chk_min_qty    CHECK (min_qty >= 2),
    CONSTRAINT chk_tier_price CHECK (price > 0),

    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    INDEX idx_bulk_tiers_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
