package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.persistence.model.ContentGroup;
import com.umc.sp.contents.persistence.model.id.ContentGroupId;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentGroupRepository extends JpaRepository<ContentGroup, ContentGroupId> {

    Optional<ContentGroup> findByIdContentId(UUID contentId);

    List<ContentGroup> findAllByIdContentIdIn(Set<UUID> contentId);
}
