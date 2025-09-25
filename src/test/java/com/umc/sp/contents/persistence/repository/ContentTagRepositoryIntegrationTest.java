package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.IntegrationTest;
import com.umc.sp.contents.persistence.model.ContentTag;
import com.umc.sp.contents.persistence.model.id.ContentTagId;
import java.time.Clock;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.*;
import static utils.ObjectTestUtils.buildCategory;
import static utils.ObjectTestUtils.buildContent;
import static utils.ObjectTestUtils.buildGenre;
import static utils.ObjectTestUtils.buildTag;

public class ContentTagRepositoryIntegrationTest implements IntegrationTest {

    @Autowired
    private ContentTagRepository contentTagRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private GenresRepository genresRepository;

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
        genresRepository.deleteAll();
        categoriesRepository.deleteAll();
        tagsRepository.deleteAll();
    }

    @Test
    void shouldSaveAndFindById() {
        //given
        var tag = tagsRepository.save(buildTag().build());
        var category = categoriesRepository.save(buildCategory().build());
        var genre = genresRepository.save(buildGenre().build());
        var content = contentRepository.save(buildContent(category, genre).build());

        var contentTag = ContentTag.builder().id(new ContentTagId(content.getId().getId(), tag.getId().getId())).disabledDate(LocalDateTime.now(clock)).build();

        //when
        var saved = contentTagRepository.save(contentTag);
        var result = contentTagRepository.findById(contentTag.getId());

        //then
        assertThat(result).get().usingRecursiveComparison().isEqualTo(saved);
        assertThat(result).get().usingRecursiveComparison().isEqualTo(contentTag);
    }
}