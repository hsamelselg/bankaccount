CREATE TABLE IF NOT EXISTS account (
    id BIGSERIAL PRIMARY KEY,
    customer_id VARCHAR(225) NOT NULL,
    country VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS balance (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    amount NUMERIC(19, 2) NOT NULL DEFAULT 0.00,
    currency VARCHAR(3) NOT NULL,
    CONSTRAINT fk_account_balance FOREIGN KEY (account_id)
        REFERENCES account(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS transaction (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    direction VARCHAR(3) NOT NULL,
    description TEXT NOT NULL,
    balance_after_transaction NUMERIC(19, 2) NOT NULL,
    CONSTRAINT fk_account_transaction FOREIGN KEY (account_id) REFERENCES account(id)
);