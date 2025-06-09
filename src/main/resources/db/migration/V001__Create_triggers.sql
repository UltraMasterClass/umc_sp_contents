CREATE EXTENSION IF NOT EXISTS "uuid-ossp" SCHEMA public;

CREATE OR REPLACE FUNCTION insert_timestamps()
RETURNS TRIGGER AS $$
BEGIN
    NEW.create_date = clock_timestamp();
    NEW.update_date = clock_timestamp();
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE OR REPLACE FUNCTION update_timestamps()
RETURNS TRIGGER AS $$
BEGIN
    NEW.create_date = OLD.create_date;
    NEW.update_date = clock_timestamp();
    RETURN NEW;
END;
$$ language 'plpgsql';