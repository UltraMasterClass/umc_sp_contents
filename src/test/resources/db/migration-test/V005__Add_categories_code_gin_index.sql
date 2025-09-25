-- V005: Add GIN index on categories.code for text search
-- TEST VERSION - Does not use CONCURRENTLY for compatibility with test environments

-- Set correct schema
SET search_path TO umc_sp;

-- Create GIN index on categories.code for category code searches
-- This index will optimize queries that search for categories by code using LIKE '%text%'
-- NOT using CONCURRENTLY in test environment
CREATE INDEX IF NOT EXISTS idx_categories_code_gin
    ON categories USING gin (lower(code) gin_trgm_ops);

-- Verify the index was created successfully
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes 
        WHERE schemaname = current_schema() 
        AND tablename = 'categories' 
        AND indexname = 'idx_categories_code_gin'
    ) THEN
        RAISE EXCEPTION 'Index idx_categories_code_gin was not created successfully';
    END IF;
END $$;
