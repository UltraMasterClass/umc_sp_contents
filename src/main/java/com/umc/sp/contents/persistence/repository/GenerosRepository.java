package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.persistence.model.Genero;
import com.umc.sp.contents.persistence.model.id.GeneroId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenerosRepository extends JpaRepository<Genero, GeneroId> {
}
