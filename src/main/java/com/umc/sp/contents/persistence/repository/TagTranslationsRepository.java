package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.persistence.model.TagTranslation;
import com.umc.sp.contents.persistence.model.id.TagTranslationId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagTranslationsRepository extends JpaRepository<TagTranslation, TagTranslationId> {
}
