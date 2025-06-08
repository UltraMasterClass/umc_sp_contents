package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.persistence.model.ContentSection;
import com.umc.sp.contents.persistence.model.id.ContentSectionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentSectionsRepository extends JpaRepository<ContentSection, ContentSectionId> {
}
