package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.persistence.model.Content;
import com.umc.sp.contents.persistence.model.id.ContentId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, ContentId> {
}
