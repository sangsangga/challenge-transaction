CREATE TABLE transactions (
  id UUID PRIMARY KEY,
  customer_id VARCHAR(64) NOT NULL,
  account_iban VARCHAR(64) NOT NULL,
  currency VARCHAR(3) NOT NULL,
  amount NUMERIC(18,2) NOT NULL,
  value_date DATE NOT NULL,
  description TEXT,
  transaction_type VARCHAR(6) NOT NULL CHECK (transaction_type IN ('CREDIT','DEBIT')),
   month_key DATE GENERATED ALWAYS AS (
    value_date - ((EXTRACT(DAY FROM value_date))::int - 1)
  ) STORED,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
  deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_txn_customer_month
  ON transactions (customer_id, month_key, value_date DESC, id);


CREATE TABLE monthly_agg (
  customer_id VARCHAR(64) NOT NULL,
  month_key DATE NOT NULL,
  currency VARCHAR(3) NOT NULL,
  total_credit NUMERIC(18,2) NOT NULL DEFAULT 0,
  total_debit  NUMERIC(18,2) NOT NULL DEFAULT 0,
  PRIMARY KEY (customer_id, month_key, currency),
  created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
  deleted_at TIMESTAMP WITH TIME ZONE
);

