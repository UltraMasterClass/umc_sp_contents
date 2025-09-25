package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.persistence.model.ContentSectionCriteria;
import com.umc.sp.contents.persistence.model.id.ContentSectionCriteriaId;
import com.umc.sp.contents.persistence.model.id.ContentSectionId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentSectionCriteriaRepository extends JpaRepository<ContentSectionCriteria, ContentSectionCriteriaId> {

    List<ContentSectionCriteria> findByContentSectionIdAndDisabledDateIsNull(ContentSectionId contentSectionId);

}
