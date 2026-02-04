-- Create budget table for category budgets
CREATE TABLE IF NOT EXISTS budget (
    id BIGSERIAL PRIMARY KEY,
    category_id BIGINT NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    period VARCHAR(50) NOT NULL, -- DAILY, WEEKLY, MONTHLY, YEARLY
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_budget_category FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE CASCADE,
    CONSTRAINT fk_budget_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT unique_budget_category_period UNIQUE (category_id, period, user_id)
);

CREATE INDEX idx_budget_user_id ON budget(user_id);
CREATE INDEX idx_budget_category_id ON budget(category_id);
