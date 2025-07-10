package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.IntegrationTest;
import com.umc.sp.contents.persistence.model.type.ContentStructureType;
import com.umc.sp.contents.persistence.model.type.ContentType;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.*;
import static utils.ObjectTestUtils.buildCategory;
import static utils.ObjectTestUtils.buildContent;
import static utils.ObjectTestUtils.buildContentGroup;
import static utils.ObjectTestUtils.buildContentInfo;
import static utils.ObjectTestUtils.buildGenre;

public class ContentRepositoryIntegrationTest implements IntegrationTest {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private ContentGroupRepository contentGroupRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private GenresRepository genresRepository;

    @Autowired
    private ContentInfoRepository contentInfoRepository;

    @Autowired
    private Clock clock;

    @BeforeEach
    void setUp() {
        cleanUp();
    }

    @AfterEach
    void cleanUp() {
        contentGroupRepository.deleteAll();
        contentInfoRepository.deleteAll();
        contentRepository.deleteAll();
        genresRepository.deleteAll();
        categoriesRepository.deleteAll();
    }

    @Test
    @Transactional
    void shouldSaveAndFindById() {
        //given
        var category = categoriesRepository.save(buildCategory().build());
        var genre = genresRepository.save(buildGenre().build());
        var contentInfo = buildContentInfo().build();
        var content = buildContent(category, genre).contentInfos(List.of(contentInfo)).build();
        contentInfo.setContent(content);

        //when
        var saved = contentRepository.save(content);
        var result = contentRepository.findById(content.getId());

        //then
        assertThat(result).get().usingRecursiveComparison().ignoringFields("contentInfos").isEqualTo(saved);
        assertThat(result).get().usingRecursiveComparison().ignoringFields("contentInfos").isEqualTo(content);
        assertThat(result.get().getContentInfos()).usingRecursiveFieldByFieldElementComparatorIgnoringFields("contentInfos").containsExactly(contentInfo);

    }

    @Test
    @Transactional
    void shouldFindByParentIdAndDisableDateIsNull() {
        //given
        var category = categoriesRepository.save(buildCategory().build());
        var genre = genresRepository.save(buildGenre().build());
        var contentInfo = buildContentInfo().build();
        var content = buildContent(category, genre).contentInfos(List.of(contentInfo)).build();
        contentInfo.setContent(content);
        content = contentRepository.save(content);
        var content2 = contentRepository.save(buildContent(category, genre).build());
        var content3 = contentRepository.save(buildContent(category, genre).build());
        var content4 = contentRepository.save(buildContent(category, genre).build());
        var content5 = contentRepository.save(buildContent(category, genre).build());

        var parentContent = contentRepository.save(buildContent(category, genre).type(ContentType.SERIES).structureType(ContentStructureType.GROUP).build());
        var contentGroup = contentGroupRepository.save(buildContentGroup(parentContent, content).sortOrder(1).build());
        var contentGroup2 = contentGroupRepository.save(buildContentGroup(parentContent, content2).sortOrder(2).build());
        var contentGroup3Disabled = contentGroupRepository.save(buildContentGroup(parentContent, content3).sortOrder(3)
                                                                                                          .disabledDate(LocalDateTime.now(clock))
                                                                                                          .build());
        var contentGroup4 = contentGroupRepository.save(buildContentGroup(parentContent, content4).sortOrder(4).build());
        var contentGroup5 = contentGroupRepository.save(buildContentGroup(parentContent, content5).sortOrder(5).build());

        //when
        var result = contentRepository.findByParentIdAndDisableDateIsNull(parentContent.getId().getId(), PageRequest.of(0, 3));

        //then
        assertThat(result.hasNext()).isTrue();
        assertThat(result.getContent()).containsExactly(content, content2, content4);

    }
}

