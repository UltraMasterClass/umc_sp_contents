package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.persistence.model.Category;
import com.umc.sp.contents.persistence.model.id.CategoryId;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoriesRepository extends JpaRepository<Category, CategoryId> {

    @Query(value = """
            SELECT NOT EXISTS (
              SELECT 1
              FROM categories child
              JOIN categories parent ON child.parent_id = parent.id
              WHERE child.id IN (:categoryIds)
              AND parent.id IN (:categoryIds)
            ) AS is_valid;
            """, nativeQuery = true)
    boolean checkCategoriesNotParentAndChildrenOfEachOther(@Param("categoryIds") Set<UUID> categoryIds);
}
