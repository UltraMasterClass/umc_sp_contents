package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.persistence.model.ContentInfo;
import com.umc.sp.contents.persistence.model.id.ContentInfoId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentInfoRepository extends JpaRepository<ContentInfo, ContentInfoId> {
}
