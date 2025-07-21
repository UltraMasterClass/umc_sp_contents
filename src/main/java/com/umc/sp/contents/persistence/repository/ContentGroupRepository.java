package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.persistence.model.ContentGroup;
import com.umc.sp.contents.persistence.model.id.ContentGroupId;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContentGroupRepository extends JpaRepository<ContentGroup, ContentGroupId> {

    List<ContentGroup> findByIdContentId(UUID contentId);

    List<ContentGroup> findAllByIdContentIdIn(Set<UUID> contentId);

    @Query(value = """
            SELECT NOT EXISTS (
              SELECT 1
              FROM content_groups
              WHERE content_id IN (:contentIds)
              AND parent_content_id IN (:contentIds)
            ) AS is_valid;
            """, nativeQuery = true)
    boolean checkContentNotParentAndChildrenOfEachOther(@Param("contentIds") Set<UUID> contentIds);
}
