CREATE TABLE IF NOT EXISTS payment_transactions (
     id bigint auto_increment NOT NULL,
     transaction_uuid VARCHAR(255),
     account_id bigint NOT NULL,
     timestamp TIMESTAMP NOT NULL,
     amount DECIMAL(100,2)
);
CREATE TABLE IF NOT EXISTS account_balance (
    id bigint auto_increment NOT NULL,
    account_id bigint NOT NULL,
    amount DECIMAL(100,2)
);