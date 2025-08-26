-- V007: Add B-tree indexes for junction tables
-- This migration is TRANSACTIONAL and safe for all environments
-- These indexes optimize JOIN operations in search queries

-- Set correct schema
SET search_path TO umc_sp;

-- Index for content_tags junction table to optimize content-to-tag lookups
CREATE INDEX IF NOT EXISTS idx_content_tags_content_id
    ON content_tags(content_id);

-- Index for content_tags junction table to optimize tag-to-content lookups  
CREATE INDEX IF NOT EXISTS idx_content_tags_tag_id
    ON content_tags(tag_id);

-- Index for content_categories junction table to optimize content-to-category lookups
CREATE INDEX IF NOT EXISTS idx_content_categories_content_id
    ON content_categories(content_id);

-- Index for content_categories junction table to optimize category-to-content lookups
CREATE INDEX IF NOT EXISTS idx_content_categories_category_id  
    ON content_categories(category_id);

-- Add comments to document the purpose of these indexes
COMMENT ON INDEX idx_content_tags_content_id IS 'B-tree index for efficient content to tag lookups';
COMMENT ON INDEX idx_content_tags_tag_id IS 'B-tree index for efficient tag to content lookups';
COMMENT ON INDEX idx_content_categories_content_id IS 'B-tree index for efficient content to category lookups';
COMMENT ON INDEX idx_content_categories_category_id IS 'B-tree index for efficient category to content lookups';

-- Verify all indexes were created
DO $$
DECLARE
    missing_indexes text[];
    index_name text;
BEGIN
    -- Check each required index
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_content_tags_content_id') THEN
        missing_indexes := array_append(missing_indexes, 'idx_content_tags_content_id');
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_content_tags_tag_id') THEN
        missing_indexes := array_append(missing_indexes, 'idx_content_tags_tag_id');
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_content_categories_content_id') THEN
        missing_indexes := array_append(missing_indexes, 'idx_content_categories_content_id');
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_content_categories_category_id') THEN
        missing_indexes := array_append(missing_indexes, 'idx_content_categories_category_id');
    END IF;
    
    -- Raise exception if any indexes are missing
    IF array_length(missing_indexes, 1) > 0 THEN
        RAISE EXCEPTION 'Failed to create indexes: %', array_to_string(missing_indexes, ', ');
    END IF;
END $$;
