-- V003: Enable pg_trgm extension for text search
-- This migration is TRANSACTIONAL and safe for all environments
-- It enables the PostgreSQL trigram extension required for GIN indexes

-- Set correct schema
SET search_path TO umc_sp;

-- Enable the pg_trgm extension (trigram matching)
-- This extension provides functions and operators for determining the similarity of text
-- and supports efficient index lookups for LIKE '%pattern%' queries
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- Verify the extension is installed
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_extension WHERE extname = 'pg_trgm') THEN
        RAISE EXCEPTION 'pg_trgm extension failed to install';
    END IF;
END $$;
