CREATE TABLE chat_rooms (
    id          VARCHAR(36)  PRIMARY KEY,
    offer_id    VARCHAR(36)  UNIQUE NOT NULL,
    buyer_id    VARCHAR(36)  NOT NULL,
    seller_id   VARCHAR(36)  NOT NULL,
    status      VARCHAR(20)  DEFAULT 'ACTIVE',
    created_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (offer_id)  REFERENCES offers(id),
    FOREIGN KEY (buyer_id)  REFERENCES users(id),
    FOREIGN KEY (seller_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE chat_messages (
    id           VARCHAR(36) PRIMARY KEY,
    chat_room_id VARCHAR(36) NOT NULL,
    sender_id    VARCHAR(36) NOT NULL,
    message      TEXT        NOT NULL,
    created_at   TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (chat_room_id) REFERENCES chat_rooms(id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id)    REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
