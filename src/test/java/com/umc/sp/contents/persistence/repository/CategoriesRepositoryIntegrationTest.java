package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.IntegrationTest;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.*;
import static utils.ObjectTestUtils.buildCategory;

public class CategoriesRepositoryIntegrationTest implements IntegrationTest {

    @Autowired
    private CategoriesRepository categoriesRepository;

    @BeforeEach
    void setUp() {
        cleanUp();
    }

    @AfterEach
    void cleanUp() {
        categoriesRepository.deleteAll();
    }

    @Test
    void shouldSaveAndFindById() {
        //given
        var category = buildCategory().build();

        //when
        var saved = categoriesRepository.save(category);
        var result = categoriesRepository.findById(category.getId());

        //then
        assertThat(result).get().isEqualTo(saved);
        assertThat(result).get().isEqualTo(category);
    }


    @Test
    void shouldCheckCategoriesNotParentAndChildrenOfEachOther() {
        //given
        var parentCategory = categoriesRepository.save(buildCategory().build());
        var category = categoriesRepository.save(buildCategory().parent(parentCategory).build());
        var category2 = categoriesRepository.save(buildCategory().build());

        //when
        var result = categoriesRepository.checkCategoriesNotParentAndChildrenOfEachOther(Set.of(category.getId().getId(), category2.getId().getId()));

        //then
        assertThat(result).isTrue();
    }


    @Test
    void shouldCheckCategoriesNotParentAndChildrenOfEachOtherAndFailIForHierarchyRelated() {
        //given
        var parentCategory = categoriesRepository.save(buildCategory().build());
        var category = categoriesRepository.save(buildCategory().parent(parentCategory).build());
        var category2 = categoriesRepository.save(buildCategory().build());

        //when
        var result = categoriesRepository.checkCategoriesNotParentAndChildrenOfEachOther(Set.of(category.getId().getId(),
                                                                                                category2.getId().getId(),
                                                                                                parentCategory.getId().getId()));

        //then
        assertThat(result).isFalse();
    }
}
