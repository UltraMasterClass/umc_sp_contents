package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.persistence.model.Genders;
import com.umc.sp.contents.persistence.model.id.GenderId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GendersRepository extends JpaRepository<Genders, GenderId> {
}
