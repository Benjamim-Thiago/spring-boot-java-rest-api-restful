ALTER TABLE users
ALTER COLUMN account_non_expired SET NOT NULL,
ALTER COLUMN account_non_locked SET NOT NULL,
ALTER COLUMN credentials_non_expired SET NOT NULL;