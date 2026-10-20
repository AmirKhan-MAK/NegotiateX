CREATE TABLE offers (
    id            VARCHAR(36)   PRIMARY KEY,
    product_id    VARCHAR(36)   NOT NULL,
    buyer_id      VARCHAR(36)   NOT NULL,
    seller_id     VARCHAR(36)   NOT NULL,
    quantity      INT           NOT NULL DEFAULT 1,
    initial_price DECIMAL(12,2) NOT NULL,
    current_price DECIMAL(12,2) NOT NULL,
    final_price   DECIMAL(12,2) NULL,
    status        ENUM('PENDING','ACCEPTED','REJECTED','COUNTERED','EXPIRED') NOT NULL DEFAULT 'PENDING',
    rounds        INT           NOT NULL DEFAULT 0,
    buyer_message TEXT          NULL,
    expires_at    TIMESTAMP     NOT NULL,
    created_at    TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT chk_rounds_max   CHECK (rounds <= 3),
    CONSTRAINT chk_quantity_pos CHECK (quantity >= 1),

    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (buyer_id)   REFERENCES users(id),
    FOREIGN KEY (seller_id)  REFERENCES users(id),

    INDEX idx_offers_buyer   (buyer_id, status),
    INDEX idx_offers_seller  (seller_id, status),
    INDEX idx_offers_product (product_id),
    INDEX idx_offers_expires (expires_at, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE offer_rounds (
    id          VARCHAR(36)   PRIMARY KEY,
    offer_id    VARCHAR(36)   NOT NULL,
    round_no    INT           NOT NULL,
    actor_id    VARCHAR(36)   NOT NULL,
    actor_role  ENUM('BUYER','SELLER', 'ADMIN') NOT NULL,
    price       DECIMAL(12,2) NOT NULL,
    action      ENUM('OFFER','COUNTER','ACCEPT','REJECT') NOT NULL,
    message     TEXT          NULL,
    created_at  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,

    UNIQUE KEY uk_offer_round (offer_id, round_no),
    FOREIGN KEY (offer_id)  REFERENCES offers(id) ON DELETE CASCADE,
    FOREIGN KEY (actor_id)  REFERENCES users(id),
    INDEX idx_offer_rounds_offer (offer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
