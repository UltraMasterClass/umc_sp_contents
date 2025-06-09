package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.persistence.model.Group;
import com.umc.sp.contents.persistence.model.id.GroupId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupsRepository extends JpaRepository<Group, GroupId> {
}
