package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.IntegrationTest;
import com.umc.sp.contents.persistence.model.Category;
import com.umc.sp.contents.persistence.model.Content;
import com.umc.sp.contents.persistence.model.ContentTag;
import com.umc.sp.contents.persistence.model.Genero;
import com.umc.sp.contents.persistence.model.Tag;
import com.umc.sp.contents.persistence.model.id.CategoryId;
import com.umc.sp.contents.persistence.model.id.ContentId;
import com.umc.sp.contents.persistence.model.id.ContentTagId;
import com.umc.sp.contents.persistence.model.id.GeneroId;
import com.umc.sp.contents.persistence.model.id.TagId;
import com.umc.sp.contents.persistence.model.type.ContentType;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static com.umc.sp.contents.persistence.model.type.CategoryType.TOPIC;

public class ContentTagRepositoryIntegrationTest implements IntegrationTest {

    @Autowired
    private ContentTagRepository contentTagRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private GenerosRepository generosRepository;

    @Autowired
    private TagsRepository tagsRepository;

    @Autowired
    private Clock clock;

    @BeforeEach
    void setUp() {
        cleanUp();
    }

    @AfterEach
    void cleanUp() {
        contentTagRepository.deleteAll();
        contentRepository.deleteAll();
        generosRepository.deleteAll();
        categoriesRepository.deleteAll();
        tagsRepository.deleteAll();
    }

    @Test
    void shouldSaveAndFindById() {
        //given
        var tag = tagsRepository.save(Tag.builder().id(new TagId()).code(UUID.randomUUID().toString()).description(UUID.randomUUID().toString()).build());
        var category = categoriesRepository.save(Category.builder()
                                                         .id(new CategoryId())
                                                         .type(TOPIC)
                                                         .description(UUID.randomUUID().toString())
                                                         .code(UUID.randomUUID().toString())
                                                         .build());
        var genero = generosRepository.save(Genero.builder()
                                                  .id(new GeneroId())
                                                  .code(UUID.randomUUID().toString())
                                                  .description(UUID.randomUUID().toString())
                                                  .build());
        var content = contentRepository.save(Content.builder()
                                                    .id(new ContentId())
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
                                                    .build());

        var contentTag = ContentTag.builder().id(new ContentTagId(content.getId().getId(), tag.getId().getId())).disableDate(LocalDateTime.now(clock)).build();

        //when
        var saved = contentTagRepository.save(contentTag);
        var result = contentTagRepository.findById(contentTag.getId());

        //then
        Assertions.assertThat(result).get().usingRecursiveComparison().isEqualTo(saved);
        Assertions.assertThat(result).get().usingRecursiveComparison().isEqualTo(contentTag);
    }
}