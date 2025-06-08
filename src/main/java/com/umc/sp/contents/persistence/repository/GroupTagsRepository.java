package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.persistence.model.GroupTag;
import com.umc.sp.contents.persistence.model.id.GroupTagId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupTagsRepository extends JpaRepository<GroupTag, GroupTagId> {
}
