package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.persistence.model.ContentTag;
import com.umc.sp.contents.persistence.model.id.ContentTagId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentTagRepository extends JpaRepository<ContentTag, ContentTagId> {
}
