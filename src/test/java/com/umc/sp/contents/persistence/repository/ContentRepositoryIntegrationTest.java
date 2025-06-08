package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.IntegrationTest;
import com.umc.sp.contents.persistence.model.Category;
import com.umc.sp.contents.persistence.model.Content;
import com.umc.sp.contents.persistence.model.Genero;
import com.umc.sp.contents.persistence.model.id.CategoryId;
import com.umc.sp.contents.persistence.model.id.ContentId;
import com.umc.sp.contents.persistence.model.id.GeneroId;
import com.umc.sp.contents.persistence.model.type.ContentType;
import java.math.BigDecimal;
import java.time.Clock;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static com.umc.sp.contents.persistence.model.type.CategoryType.TOPIC;

public class ContentRepositoryIntegrationTest implements IntegrationTest {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private GenerosRepository generosRepository;

    @Autowired
    private Clock clock;

    @BeforeEach
    void setUp() {
        cleanUp();
    }

    @AfterEach
    void cleanUp() {
        contentRepository.deleteAll();
        generosRepository.deleteAll();
        categoriesRepository.deleteAll();
    }

    @Test
    void shouldSaveAndFindById() {
        //given
        var category = categoriesRepository.save(Category.builder().id(new CategoryId()).type(TOPIC).description(UUID.randomUUID().toString()).code(UUID.randomUUID().toString()).build());
        var genero = generosRepository.save(Genero.builder().id(new GeneroId()).code(UUID.randomUUID().toString()).description(UUID.randomUUID().toString()).build());

        //when
        var content = Content.builder().id(new ContentId())
                             .featured(true)
                             .type(ContentType.VIDEO)
                             .category(category)
                             .name(UUID.randomUUID().toString())
                             .description(UUID.randomUUID().toString())
                             .genero(genero)
                             .especialidadId(UUID.randomUUID())
                             .resourceUrl(UUID.randomUUID().toString())
                             .cdnUrl(UUID.randomUUID().toString())
                             .rating(BigDecimal.valueOf(4.5))
                             .duration("1:30:00")
                             .build();

        //when
        var saved = contentRepository.save(content);
        var result = contentRepository.findById(content.getId());

        //then
        Assertions.assertThat(result).get().usingRecursiveComparison().isEqualTo(saved);
        Assertions.assertThat(result).get().usingRecursiveComparison().isEqualTo(content);
    }
}

