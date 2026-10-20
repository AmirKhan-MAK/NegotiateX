-- V10__add_ai_negotiation_fields.sql
ALTER TABLE products
ADD COLUMN ai_negotiation_enabled BOOLEAN DEFAULT FALSE,
ADD COLUMN ai_target_price DECIMAL(12, 2) NULL;

ALTER TABLE offers
ADD COLUMN ai_escalated BOOLEAN DEFAULT FALSE;
