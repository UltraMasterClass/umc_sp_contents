package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.IntegrationTest;
import com.umc.sp.contents.persistence.model.Category;
import com.umc.sp.contents.persistence.model.id.CategoryId;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static com.umc.sp.contents.persistence.model.type.CategoryType.TOPIC;

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
        var category = Category.builder().id(new CategoryId()).type(TOPIC).description(UUID.randomUUID().toString()).code(UUID.randomUUID().toString()).build();

        //when
        var saved = categoriesRepository.save(category);
        var result = categoriesRepository.findById(category.getId());

        //then
        Assertions.assertThat(result).get().isEqualTo(saved);
        Assertions.assertThat(result).get().isEqualTo(category);
    }
}
