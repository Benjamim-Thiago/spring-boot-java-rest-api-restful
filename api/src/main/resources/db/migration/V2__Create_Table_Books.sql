CREATE TABLE books (
  id SERIAL PRIMARY KEY NOT NULL,
  author TEXT,
  launch_date TIMESTAMPTZ NOT NULL,
  price NUMERIC(6, 2) NOT NULL,
  title TEXT
);