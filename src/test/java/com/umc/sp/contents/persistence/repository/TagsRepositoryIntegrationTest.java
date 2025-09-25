package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.IntegrationTest;
import com.umc.sp.contents.persistence.model.ContentTag;
import com.umc.sp.contents.persistence.model.Tag;
import com.umc.sp.contents.persistence.model.id.ContentTagId;
import com.umc.sp.contents.persistence.model.id.TagId;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.*;
import static utils.ObjectTestUtils.buildCategory;
import static utils.ObjectTestUtils.buildContent;
import static utils.ObjectTestUtils.buildGenre;
import static utils.ObjectTestUtils.buildTag;

public class TagsRepositoryIntegrationTest implements IntegrationTest {

    @Autowired
    private TagsRepository tagsRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private GenresRepository genresRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private ContentTagRepository contentTagRepository;


    @Autowired
    private Clock clock;

    @BeforeEach
    void setUp() {
        cleanUp();
    }

    @AfterEach
    void cleanUp() {
        contentTagRepository.deleteAll();
        tagsRepository.deleteAll();
        contentRepository.deleteAll();
        genresRepository.deleteAll();
        categoriesRepository.deleteAll();
    }

    @Test
    void shouldSaveAndFindById() {
        //given
        var tag = Tag.builder().id(new TagId()).code(UUID.randomUUID().toString()).description(UUID.randomUUID().toString()).build();

        //when
        var saved = tagsRepository.save(tag);
        var result = tagsRepository.findById(tag.getId());

        //then
        assertThat(result).get().isEqualTo(saved);
        assertThat(result).get().isEqualTo(tag);
    }

    @Test
    void shouldFindContentTagsByContentId() {
        // given
        var category = categoriesRepository.save(buildCategory().build());
        var genre = genresRepository.save(buildGenre().build());
        var content = contentRepository.save(buildContent(category, genre).build());

        var tag = tagsRepository.save(buildTag().build());
        var tag2 = tagsRepository.save(buildTag().build());

        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content.getId().getId(), tag.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder()
                                            .id(new ContentTagId(content.getId().getId(), tag2.getId().getId()))
                                            .disabledDate(LocalDateTime.now(clock))
                                            .build());

        // when
        var result = tagsRepository.findContentTagsByContentId(content.getId().getId());

        // then
        assertThat(result).containsExactly(tag);
    }
}
