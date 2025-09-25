package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.persistence.model.ContentSection;
import com.umc.sp.contents.persistence.model.id.ContentSectionId;
import com.umc.sp.contents.persistence.model.type.ContentSectionViewType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentSectionsRepository extends JpaRepository<ContentSection, ContentSectionId> {

    Slice<ContentSection> findByViewTypeAndDisabledDateIsNull(ContentSectionViewType viewType, Pageable pageable);
}
