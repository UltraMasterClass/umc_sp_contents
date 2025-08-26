-- V006: Add GIN indexes on tag_translations for text search
-- This migration uses CREATE INDEX CONCURRENTLY for zero-downtime index creation
-- IMPORTANT: This requires spring.flyway.mixed=true in application properties

-- Disable transactional execution for this script
-- @FLYWAY:mixed=true

-- Set correct schema
SET search_path TO umc_sp;

-- Create GIN index on tag_translations.value for tag value searches
-- This index will optimize queries that search for tags by translated value using LIKE '%text%'
-- Using CONCURRENTLY to avoid table locks in production
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_tag_translations_value_gin
    ON tag_translations USING gin (lower(value) gin_trgm_ops);

-- Additional composite index for tag_translations to optimize joins
-- This helps when searching tags by language_code and value together
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_tag_translations_language_value
    ON tag_translations(language_code, lower(value));

-- Verify the indexes were created successfully
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes 
        WHERE schemaname = current_schema() 
        AND tablename = 'tag_translations' 
        AND indexname = 'idx_tag_translations_value_gin'
    ) THEN
        RAISE EXCEPTION 'Index idx_tag_translations_value_gin was not created successfully';
    END IF;
    
    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes 
        WHERE schemaname = current_schema() 
        AND tablename = 'tag_translations' 
        AND indexname = 'idx_tag_translations_language_value'
    ) THEN
        RAISE EXCEPTION 'Index idx_tag_translations_language_value was not created successfully';
    END IF;
END $$;
