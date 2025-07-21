package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.IntegrationTest;
import com.umc.sp.contents.persistence.model.type.ContentStructureType;
import com.umc.sp.contents.persistence.model.type.ContentType;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.*;
import static utils.ObjectTestUtils.buildCategory;
import static utils.ObjectTestUtils.buildContent;
import static utils.ObjectTestUtils.buildContentGroup;
import static utils.ObjectTestUtils.buildGenre;

public class ContentGroupsRepositoryIntegrationTest implements IntegrationTest {

    @Autowired
    private ContentGroupRepository contentGroupRepository;

    @Autowired
    private GenresRepository genresRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private Clock clock;

    @BeforeEach
    void setUp() {
        cleanUp();
    }

    @AfterEach
    void cleanUp() {
        contentGroupRepository.deleteAll();
        contentRepository.deleteAll();
        genresRepository.deleteAll();
        categoriesRepository.deleteAll();
    }

    @Test
    void shouldSaveAndFindById() {
        //given
        var category = categoriesRepository.save(buildCategory().build());
        var genre = genresRepository.save(buildGenre().build());
        var parentContent = contentRepository.save(buildContent(category, genre).type(ContentType.SERIES).structureType(ContentStructureType.GROUP).build());
        var content = contentRepository.save(buildContent(category, genre).build());
        var contentGroup = buildContentGroup(parentContent, content).disabledDate(LocalDateTime.now(clock)).build();

        //when
        var saved = contentGroupRepository.save(contentGroup);
        var result = contentGroupRepository.findById(contentGroup.getId());

        //then
        assertThat(result).get().isEqualTo(saved);
        assertThat(result).get().isEqualTo(contentGroup);
    }

    @Test
    void shouldFindByIdContentId() {
        //given
        var category = categoriesRepository.save(buildCategory().build());
        var genre = genresRepository.save(buildGenre().build());
        var parentContent = contentRepository.save(buildContent(category, genre).type(ContentType.SERIES).structureType(ContentStructureType.GROUP).build());
        var content = contentRepository.save(buildContent(category, genre).build());
        var content2 = contentRepository.save(buildContent(category, genre).build());
        var contentGroup = contentGroupRepository.save(buildContentGroup(parentContent, content).disabledDate(LocalDateTime.now(clock)).build());
        var contentGroup2 = contentGroupRepository.save(buildContentGroup(parentContent, content2).disabledDate(LocalDateTime.now(clock)).build());

        //when
        var result = contentGroupRepository.findByIdContentId(content.getId().getId());

        //then
        assertThat(result).usingRecursiveFieldByFieldElementComparator().containsExactly(contentGroup);
    }

    @Test
    void shouldFindAllByIdContentIdIn() {
        //given
        var category = categoriesRepository.save(buildCategory().build());
        var genre = genresRepository.save(buildGenre().build());
        var parentContent = contentRepository.save(buildContent(category, genre).type(ContentType.SERIES).structureType(ContentStructureType.GROUP).build());
        var content = contentRepository.save(buildContent(category, genre).build());
        var content2 = contentRepository.save(buildContent(category, genre).build());
        var content3 = contentRepository.save(buildContent(category, genre).build());
        var contentGroup = contentGroupRepository.save(buildContentGroup(parentContent, content).disabledDate(LocalDateTime.now(clock)).build());
        var contentGroup2 = contentGroupRepository.save(buildContentGroup(parentContent, content2).disabledDate(LocalDateTime.now(clock)).build());
        var contentGroup3 = contentGroupRepository.save(buildContentGroup(parentContent, content3).disabledDate(LocalDateTime.now(clock)).build());

        //when
        var result = contentGroupRepository.findAllByIdContentIdIn(Set.of(content.getId().getId(), content3.getId().getId()));

        //then
        assertThat(result).containsExactlyInAnyOrder(contentGroup, contentGroup3);
    }


    @Test
    void shouldCheckContentNotParentAndChildrenOfEachOther() {
        //given
        var category = categoriesRepository.save(buildCategory().build());
        var genre = genresRepository.save(buildGenre().build());
        var parentContent = contentRepository.save(buildContent(category, genre).type(ContentType.SERIES).structureType(ContentStructureType.GROUP).build());
        var content = contentRepository.save(buildContent(category, genre).build());
        var content2 = contentRepository.save(buildContent(category, genre).build());
        var content3 = contentRepository.save(buildContent(category, genre).build());
        var contentGroup = contentGroupRepository.save(buildContentGroup(parentContent, content).disabledDate(LocalDateTime.now(clock)).build());
        var contentGroup2 = contentGroupRepository.save(buildContentGroup(parentContent, content2).disabledDate(LocalDateTime.now(clock)).build());
        var contentGroup3 = contentGroupRepository.save(buildContentGroup(parentContent, content3).disabledDate(LocalDateTime.now(clock)).build());

        //when
        var result = contentGroupRepository.checkContentNotParentAndChildrenOfEachOther(Set.of(content.getId().getId(), content3.getId().getId()));

        //then
        assertThat(result).isTrue();
    }


    @Test
    void shouldCheckContentNotParentAndChildrenOfEachOtherAndFailIForHierarchyRelated() {
        //given
        var category = categoriesRepository.save(buildCategory().build());
        var genre = genresRepository.save(buildGenre().build());
        var parentContent = contentRepository.save(buildContent(category, genre).type(ContentType.SERIES).structureType(ContentStructureType.GROUP).build());
        var content = contentRepository.save(buildContent(category, genre).build());
        var content2 = contentRepository.save(buildContent(category, genre).build());
        var content3 = contentRepository.save(buildContent(category, genre).build());
        var contentGroup = contentGroupRepository.save(buildContentGroup(parentContent, content).disabledDate(LocalDateTime.now(clock)).build());
        var contentGroup2 = contentGroupRepository.save(buildContentGroup(parentContent, content2).disabledDate(LocalDateTime.now(clock)).build());
        var contentGroup3 = contentGroupRepository.save(buildContentGroup(parentContent, content3).disabledDate(LocalDateTime.now(clock)).build());

        //when
        var result = contentGroupRepository.checkContentNotParentAndChildrenOfEachOther(Set.of(content.getId().getId(),
                                                                                               content3.getId().getId(),
                                                                                               parentContent.getId().getId()));

        //then
        assertThat(result).isFalse();
    }
}
