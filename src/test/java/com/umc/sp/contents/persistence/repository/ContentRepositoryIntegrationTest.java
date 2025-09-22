package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.IntegrationTest;
import com.umc.sp.contents.persistence.model.ContentTag;
import com.umc.sp.contents.persistence.model.TagTranslation;
import com.umc.sp.contents.persistence.model.id.CategoryId;
import com.umc.sp.contents.persistence.model.id.ContentTagId;
import com.umc.sp.contents.persistence.model.id.TagTranslationId;
import com.umc.sp.contents.persistence.model.type.ContentStructureType;
import com.umc.sp.contents.persistence.model.type.ContentType;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import static com.umc.sp.contents.persistence.specification.ContentSpecifications.hasCategories;
import static com.umc.sp.contents.persistence.specification.ContentSpecifications.hasTags;
import static com.umc.sp.contents.persistence.specification.ContentSpecifications.searchOnTitleOrCategoryOrTagContains;
import static org.assertj.core.api.Assertions.*;
import static utils.ObjectTestUtils.buildCategory;
import static utils.ObjectTestUtils.buildContent;
import static utils.ObjectTestUtils.buildContentGroup;
import static utils.ObjectTestUtils.buildContentInfo;
import static utils.ObjectTestUtils.buildGenre;
import static utils.ObjectTestUtils.buildTag;

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
    private ContentTagRepository contentTagRepository;

    @Autowired
    private TagsRepository tagsRepository;

    @Autowired
    private TagTranslationsRepository tagTranslationsRepository;

    @Autowired
    private Clock clock;

    @BeforeEach
    void setUp() {
        cleanUp();
    }

    @AfterEach
    void cleanUp() {
        tagTranslationsRepository.deleteAll();
        contentTagRepository.deleteAll();
        contentGroupRepository.deleteAll();
        contentInfoRepository.deleteAll();
        contentRepository.deleteAll();
        genresRepository.deleteAll();
        categoriesRepository.deleteAll();
        tagsRepository.deleteAll();
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


    @Test
    @Transactional
    void shouldFindAllByTag() {
        //given
        var tag = tagsRepository.save(buildTag().build());
        var tagWithDifferentText = tagsRepository.save(buildTag().build());
        var category = categoriesRepository.save(buildCategory().build());
        var genre = genresRepository.save(buildGenre().build());

        var content = contentRepository.save(buildContent(category, genre).build());
        var content2 = contentRepository.save(buildContent(category, genre).build());
        var content3 = contentRepository.save(buildContent(category, genre).build());

        var parentContent = contentRepository.save(buildContent(category, genre).type(ContentType.SERIES).structureType(ContentStructureType.GROUP).build());
        contentGroupRepository.save(buildContentGroup(parentContent, content).sortOrder(1).build());
        contentGroupRepository.save(buildContentGroup(parentContent, content2).sortOrder(2).build());

        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content.getId().getId(), tag.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content2.getId().getId(), tagWithDifferentText.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content3.getId().getId(), tag.getId().getId())).build());

        //when
        var contents = contentRepository.findAll(hasTags(Set.of(tag.getId().getId())), Pageable.unpaged());

        //then
        assertThat(contents.getContent()).containsExactlyInAnyOrder(content, content3);
    }

    @Test
    @Transactional
    void shouldFindAllByCategories() {
        //given
        var categoryId = new CategoryId();
        var category = categoriesRepository.save(buildCategory().id(categoryId).build());
        var categoryDifferentText = categoriesRepository.save(buildCategory().build());
        var genre = genresRepository.save(buildGenre().build());

        var content = contentRepository.save(buildContent(category, genre).categories(List.of(category, categoryDifferentText)).build());
        var content2 = contentRepository.save(buildContent(categoryDifferentText, genre).build());
        var content3 = contentRepository.save(buildContent(category, genre).build());

        var parentContent = contentRepository.save(buildContent(category, genre).type(ContentType.SERIES).structureType(ContentStructureType.GROUP).build());
        contentGroupRepository.save(buildContentGroup(parentContent, content).sortOrder(1).build());
        contentGroupRepository.save(buildContentGroup(parentContent, content2).sortOrder(2).build());

        //when
        var contents = contentRepository.findAll(hasCategories(Set.of(categoryId.getId())), Pageable.unpaged());

        //then
        assertThat(contents.getContent()).containsExactlyInAnyOrder(parentContent, content, content3);
    }

    @Test
    @Transactional
    void shouldFindAllBySearchText() {
        //given
        var text = UUID.randomUUID().toString();
        var tag = tagsRepository.save(buildTag().build());
        var tag2 = tagsRepository.save(buildTag().build());
        var tagWithDifferentText = tagsRepository.save(buildTag().build());

        var tagTranslation = TagTranslation.builder().id(new TagTranslationId(tag.getId(), "es")).value(text + UUID.randomUUID()).tag(tag).build();
        var tagTranslation2 = TagTranslation.builder().id(new TagTranslationId(tag2.getId(), "es")).value(UUID.randomUUID() + text).tag(tag2).build();
        var tagTranslationDifferent = TagTranslation.builder()
                                                    .id(new TagTranslationId(tagWithDifferentText.getId(), "es"))
                                                    .value(UUID.randomUUID().toString())
                                                    .tag(tagWithDifferentText)
                                                    .build();

        tagTranslationsRepository.saveAll(List.of(tagTranslation, tagTranslation2, tagTranslationDifferent));

        // NOTA: La búsqueda por category.code Y content.name está desactivada en ContentSpecifications por performance
        // Las siguientes líneas crean categorías con código que contiene el texto de búsqueda,
        // pero NO serán encontradas por la búsqueda actual
        var category = categoriesRepository.save(buildCategory().code(UUID.randomUUID() + text).build());
        var category2 = categoriesRepository.save(buildCategory().code(text + UUID.randomUUID()).build());
        var categoryDifferentText = categoriesRepository.save(buildCategory().build());

        var genre = genresRepository.save(buildGenre().build());

        var contentInfo = buildContentInfo().build();
        var content = buildContent(category, genre).contentInfos(List.of(contentInfo)).build();
        contentInfo.setContent(content);
        content = contentRepository.save(content);
        // content2 y content3 NO serán encontrados porque la búsqueda por category.code está desactivada
        var content2 = contentRepository.save(buildContent(category, genre).build());
        var content3 = contentRepository.save(buildContent(category2, genre).build());
        // excluded tag, category and title do not contain the text to search
        var content4 = contentRepository.save(buildContent(categoryDifferentText, genre).build());
        var content5 = contentRepository.save(buildContent(categoryDifferentText, genre).build());

        // parentContent NO será encontrado porque la búsqueda por content.name está desactivada
        var parentContent = contentRepository.save(buildContent(categoryDifferentText, genre).name(UUID.randomUUID() + text + UUID.randomUUID())
                                                                                             .type(ContentType.SERIES)
                                                                                             .structureType(ContentStructureType.GROUP)
                                                                                             .build());
        contentGroupRepository.save(buildContentGroup(parentContent, content).sortOrder(1).build());
        contentGroupRepository.save(buildContentGroup(parentContent, content2).sortOrder(2).build());
        contentGroupRepository.save(buildContentGroup(parentContent, content3).sortOrder(3).disabledDate(LocalDateTime.now(clock)).build());
        contentGroupRepository.save(buildContentGroup(parentContent, content4).sortOrder(4).build());
        contentGroupRepository.save(buildContentGroup(parentContent, content5).sortOrder(5).build());

        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content.getId().getId(), tag.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content5.getId().getId(), tag2.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content4.getId().getId(), tagWithDifferentText.getId().getId())).build());

        //when
        var contents = contentRepository.findAll(searchOnTitleOrCategoryOrTagContains(text, "es"), Pageable.unpaged());

        //then
        // ACTUALIZADO: Solo esperamos content (por tag) y content5 (por tag)
        // parentContent NO se encuentra porque la búsqueda por content.name está desactivada
        // content2 y content3 NO se encuentran porque la búsqueda por category.code está desactivada
        assertThat(contents.getContent()).usingRecursiveFieldByFieldElementComparatorIgnoringFields(".contentInfos")
                                         .containsExactlyInAnyOrder(content, content5);
    }
}

