package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.persistence.model.Content;
import com.umc.sp.contents.persistence.model.id.ContentId;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContentRepository extends JpaRepository<Content, ContentId>, JpaSpecificationExecutor<Content> {

    @Query(value = """
            SELECT c.*
            FROM contents c
            INNER JOIN content_groups cg ON cg.content_id = c.id
            INNER JOIN contents cp ON cg.parent_content_id = cp.id
            WHERE cp.id = :parentContentId
            AND cg.disabled_date IS NULL
            ORDER BY cg.sort_order ASC
            """, nativeQuery = true)
    Slice<Content> findByParentIdAndDisableDateIsNull(@Param("parentContentId") UUID parentContentId, Pageable page);
}
