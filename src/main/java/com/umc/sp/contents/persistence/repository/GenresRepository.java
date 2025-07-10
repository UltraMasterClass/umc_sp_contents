package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.persistence.model.Genre;
import com.umc.sp.contents.persistence.model.id.GenresId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenresRepository extends JpaRepository<Genre, GenresId> {
}
