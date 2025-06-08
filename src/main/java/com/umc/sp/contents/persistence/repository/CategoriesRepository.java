package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.persistence.model.Category;
import com.umc.sp.contents.persistence.model.id.CategoryId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriesRepository extends JpaRepository<Category, CategoryId> {
}
