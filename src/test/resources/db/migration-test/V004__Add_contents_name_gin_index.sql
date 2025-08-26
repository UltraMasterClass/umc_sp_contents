-- V004: Add GIN index on contents.name for text search
-- TEST VERSION - Does not use CONCURRENTLY for compatibility with test environments

-- Set correct schema
SET search_path TO umc_sp;

-- Create GIN index on contents.name for title searches
-- This index will optimize queries that search for content by name using LIKE '%text%'
-- NOT using CONCURRENTLY in test environment
CREATE INDEX IF NOT EXISTS idx_contents_name_gin
    ON contents USING gin (lower(name) gin_trgm_ops);

-- Verify the index was created successfully
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes 
        WHERE schemaname = current_schema() 
        AND tablename = 'contents' 
        AND indexname = 'idx_contents_name_gin'
    ) THEN
        RAISE EXCEPTION 'Index idx_contents_name_gin was not created successfully';
    END IF;
END $$;
