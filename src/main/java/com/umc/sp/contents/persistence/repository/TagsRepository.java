package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.persistence.model.Tag;
import com.umc.sp.contents.persistence.model.id.TagId;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TagsRepository extends JpaRepository<Tag, TagId> {

    @Query(value = """
            SELECT t.*
            FROM tags t
            INNER JOIN content_tags ct ON ct.tag_id = t.id
            INNER JOIN contents c ON ct.content_id = c.id
            WHERE ct.disabled_date IS NULL
            AND c.id = :contentId
            """, nativeQuery = true)
    List<Tag> findContentTagsByContentId(@Param("contentId") UUID contentId);
}
