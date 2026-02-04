-- Create purchase table for finance tracking
CREATE TABLE IF NOT EXISTS purchase (
    id BIGSERIAL PRIMARY KEY,
    purchase_date DATE NOT NULL,
    merchant VARCHAR(255) NOT NULL,
    merchant_address TEXT,
    price DECIMAL(19, 2) NOT NULL,
    vat DECIMAL(19, 2),
    total DECIMAL(19, 2) NOT NULL,
    quantity INTEGER DEFAULT 1,
    link TEXT,
    category_id BIGINT,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_purchase_category FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE SET NULL,
    CONSTRAINT fk_purchase_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_purchase_user_id ON purchase(user_id);
CREATE INDEX idx_purchase_category_id ON purchase(category_id);
CREATE INDEX idx_purchase_date ON purchase(purchase_date);
