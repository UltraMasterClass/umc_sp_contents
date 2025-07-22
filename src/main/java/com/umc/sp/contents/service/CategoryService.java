package com.umc.sp.contents.service;

import com.umc.sp.contents.exception.ConflictException;
import com.umc.sp.contents.persistence.model.Category;
import com.umc.sp.contents.persistence.model.id.CategoryId;
import com.umc.sp.contents.persistence.repository.CategoriesRepository;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoriesRepository categoriesRepository;

    @Transactional(readOnly = true)
    public void checkCategoriesNotParentAndChildrenOfEachOther(Set<UUID> categoryIds) {
        if (CollectionUtils.isEmpty(categoryIds)) {
            return;
        }

        if (!categoriesRepository.checkCategoriesNotParentAndChildrenOfEachOther(categoryIds)) {
            throw new ConflictException("Given categories must not be parents of each other");
        }
    }

    @Transactional(readOnly = true)
    public List<Category> getCategoriesByIds(Set<UUID> categoryIds){
        return categoriesRepository.findAllById(categoryIds.stream().map(CategoryId::new).collect(Collectors.toSet()));
    }

}
