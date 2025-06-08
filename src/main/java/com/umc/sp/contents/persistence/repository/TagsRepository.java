package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.persistence.model.Tag;
import com.umc.sp.contents.persistence.model.id.TagId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagsRepository extends JpaRepository<Tag, TagId> {
}
