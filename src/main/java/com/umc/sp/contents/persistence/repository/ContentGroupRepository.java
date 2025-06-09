package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.persistence.model.ContentGroup;
import com.umc.sp.contents.persistence.model.id.ContentGroupId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentGroupRepository extends JpaRepository<ContentGroup, ContentGroupId> {
}
