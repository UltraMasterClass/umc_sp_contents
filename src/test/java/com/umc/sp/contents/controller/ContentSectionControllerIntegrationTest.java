package com.umc.sp.contents.controller;

import com.umc.sp.contents.IntegrationTest;
import com.umc.sp.contents.dto.response.ContentSectionsDto;
import com.umc.sp.contents.dto.response.ContentsDto;
import com.umc.sp.contents.dto.response.ExplorerDto;
import com.umc.sp.contents.exception.ErrorResponse;
import com.umc.sp.contents.mapper.ContentMapper;
import com.umc.sp.contents.mapper.ContentSectionMapper;
import com.umc.sp.contents.persistence.model.ContentTag;
import com.umc.sp.contents.persistence.model.id.CategoryId;
import com.umc.sp.contents.persistence.model.id.ContentTagId;
import com.umc.sp.contents.persistence.model.id.TagId;
import com.umc.sp.contents.persistence.model.type.ContentSectionCriteriaType;
import com.umc.sp.contents.persistence.model.type.ContentSectionType;
import com.umc.sp.contents.persistence.model.type.ContentStructureType;
import com.umc.sp.contents.persistence.model.type.ContentType;
import com.umc.sp.contents.persistence.repository.CategoriesRepository;
import com.umc.sp.contents.persistence.repository.ContentGroupRepository;
import com.umc.sp.contents.persistence.repository.ContentInfoRepository;
import com.umc.sp.contents.persistence.repository.ContentRepository;
import com.umc.sp.contents.persistence.repository.ContentSectionCriteriaRepository;
import com.umc.sp.contents.persistence.repository.ContentSectionsRepository;
import com.umc.sp.contents.persistence.repository.ContentTagRepository;
import com.umc.sp.contents.persistence.repository.GenresRepository;
import com.umc.sp.contents.persistence.repository.TagsRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import static com.umc.sp.contents.persistence.model.type.ContentSectionCriteriaRelationType.AND;
import static com.umc.sp.contents.persistence.model.type.ContentSectionCriteriaRelationType.NOT;
import static com.umc.sp.contents.persistence.model.type.ContentSectionViewType.DISCOVERY;
import static org.assertj.core.api.Assertions.assertThat;
import static utils.ObjectTestUtils.buildCategory;
import static utils.ObjectTestUtils.buildContent;
import static utils.ObjectTestUtils.buildContentGroup;
import static utils.ObjectTestUtils.buildContentSection;
import static utils.ObjectTestUtils.buildContentSectionCriteria;
import static utils.ObjectTestUtils.buildGenre;
import static utils.ObjectTestUtils.buildTag;

public class ContentSectionControllerIntegrationTest implements IntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ContentTagRepository contentTagRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private ContentGroupRepository contentGroupRepository;

    @Autowired
    private ContentInfoRepository contentInfoRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private GenresRepository genresRepository;

    @Autowired
    private TagsRepository tagsRepository;

    @Autowired
    private ContentSectionCriteriaRepository contentSectionCriteriaRepository;

    @Autowired
    private ContentSectionsRepository contentSectionsRepository;

    @Autowired
    private ContentMapper contentMapper;

    @Autowired
    private ContentSectionMapper contentSectionMapper;

    @Autowired
    private Clock clock;

    @BeforeEach
    void setUp() {
        cleanUp();
    }

    @AfterEach
    void cleanUp() {
        contentSectionCriteriaRepository.deleteAll();
        contentSectionsRepository.deleteAll();
        contentGroupRepository.deleteAll();
        contentInfoRepository.deleteAll();
        contentTagRepository.deleteAll();
        contentRepository.deleteAll();
        genresRepository.deleteAll();
        categoriesRepository.deleteAll();
        tagsRepository.deleteAll();
    }


    @Test
    void shouldGetContentSectionsByViewType() {
        //given
        var text = UUID.randomUUID().toString();
        var categoryId = new CategoryId();
        var tagId = new TagId();

        var tag = tagsRepository.save(buildTag().id(tagId).build());
        var tag2 = tagsRepository.save(buildTag().build());
        var excludedTag = tagsRepository.save(buildTag().build());

        var category = categoriesRepository.save(buildCategory().id(categoryId).build());
        var category2 = categoriesRepository.save(buildCategory().code(text + UUID.randomUUID()).build());
        var category3 = categoriesRepository.save(buildCategory().code(UUID.randomUUID().toString()).build());
        var genre = genresRepository.save(buildGenre().build());

        var content = contentRepository.save(buildContent(category, genre).build());
        var content2 = contentRepository.save(buildContent(category, genre).name(UUID.randomUUID() + text + UUID.randomUUID()).build());
        var content3 = contentRepository.save(buildContent(category, genre).build());
        var content4WithExcludedTag = contentRepository.save(buildContent(category, genre).categories(List.of(category, category3)).build());
        var content5WitDifferentCategory = contentRepository.save(buildContent(category2, genre).build());

        var parentContent = contentRepository.save(buildContent(category, genre).name(UUID.randomUUID() + text + UUID.randomUUID())
                                                                                .type(ContentType.SERIES)
                                                                                .structureType(ContentStructureType.GROUP)
                                                                                .build());
        contentGroupRepository.save(buildContentGroup(parentContent, content).sortOrder(1).build());
        contentGroupRepository.save(buildContentGroup(parentContent, content2).sortOrder(2).build());
        contentGroupRepository.save(buildContentGroup(parentContent, content3).sortOrder(3).disabledDate(LocalDateTime.now(clock)).build());
        contentGroupRepository.save(buildContentGroup(parentContent, content4WithExcludedTag).sortOrder(4).build());
        contentGroupRepository.save(buildContentGroup(parentContent, content5WitDifferentCategory).sortOrder(5).build());

        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(parentContent.getId().getId(), tag.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(parentContent.getId().getId(), tag2.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content.getId().getId(), tag.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content.getId().getId(), tag2.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content2.getId().getId(), tag.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content2.getId().getId(), tag2.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content3.getId().getId(), tag.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content4WithExcludedTag.getId().getId(), tag.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content4WithExcludedTag.getId().getId(), excludedTag.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content5WitDifferentCategory.getId().getId(), tag.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content5WitDifferentCategory.getId().getId(), tag2.getId().getId())).build());

        var contentSection = contentSectionsRepository.save(buildContentSection().numberOfElements(10).build());
        var contentSectionCriteria = contentSectionCriteriaRepository.save(buildContentSectionCriteria(contentSection.getId()).relationType(AND)
                                                                                                                              .referenceIds(tag.getId() +
                                                                                                                                            "," +
                                                                                                                                            tag2.getId())
                                                                                                                              .build());
        var contentSectionCriteria2 = contentSectionCriteriaRepository.save(buildContentSectionCriteria(contentSection.getId()).type(ContentSectionCriteriaType.TAG)
                                                                                                                               .referenceIds(excludedTag.getId()
                                                                                                                                                        .toString())
                                                                                                                               .relationType(NOT)
                                                                                                                               .build());
        var contentSectionCriteria3 = contentSectionCriteriaRepository.save(buildContentSectionCriteria(contentSection.getId()).type(ContentSectionCriteriaType.CATEGORY)
                                                                                                                               .relationType(AND)
                                                                                                                               .referenceIds(category.getId()
                                                                                                                                                     .toString())
                                                                                                                               .build());
        var expectedContent1 = contentMapper.convertToDto(content, Set.of(parentContent.getId().getId())).get();
        var expectedContent2 = contentMapper.convertToDto(content2, Set.of(parentContent.getId().getId())).get();
        var expectedContent3 = contentMapper.convertToDto(content3, Set.of(parentContent.getId().getId())).get();
        var expectedContent4 = contentMapper.convertToDto(parentContent, null).get();


        //when
        var result = webTestClient.get()
                                  .uri(String.format("/sections/%s/main", DISCOVERY))
                                  .accept(MediaType.APPLICATION_JSON)
                                  .exchange()
                                  .expectStatus()
                                  .isOk()
                                  .expectBody(ContentSectionsDto.class)
                                  .returnResult()
                                  .getResponseBody();

        //then
        assertThat(result).isNotNull();
        assertThat(result.isHasNext()).isFalse();
        assertThat(result.getViewType()).isEqualTo(DISCOVERY);
        assertThat(result.getSections()).containsExactlyInAnyOrder(contentSectionMapper.buildContentSectionDto(contentSection,
                                                                                                               List.of(expectedContent4,
                                                                                                                       expectedContent3,
                                                                                                                       expectedContent2,
                                                                                                                       expectedContent1),
                                                                                                               false,
                                                                                                               Set.of(tag.getId().getId(),
                                                                                                                      tag2.getId().getId()),
                                                                                                               Set.of(category.getId().getId()),
                                                                                                               Set.of(excludedTag.getId().getId()),
                                                                                                               Set.of(),
                                                                                                               0,
                                                                                                               contentSection.getNumberOfElements()));
    }

    @Test
    void shouldGetContentSectionsByViewTypeAndReturnExplorerOnSectionIfMoreContentAvailable() {
        //given
        var text = UUID.randomUUID().toString();
        var categoryId = new CategoryId();
        var tagId = new TagId();

        var tag = tagsRepository.save(buildTag().id(tagId).build());
        var tag2 = tagsRepository.save(buildTag().build());
        var excludedTag = tagsRepository.save(buildTag().build());

        var category = categoriesRepository.save(buildCategory().id(categoryId).build());
        var category2 = categoriesRepository.save(buildCategory().code(text + UUID.randomUUID()).build());
        var category3 = categoriesRepository.save(buildCategory().code(UUID.randomUUID().toString()).build());
        var genre = genresRepository.save(buildGenre().build());

        var content = contentRepository.save(buildContent(category, genre).build());
        var content2 = contentRepository.save(buildContent(category, genre).name(UUID.randomUUID() + text + UUID.randomUUID()).build());
        var content3 = contentRepository.save(buildContent(category, genre).build());
        var content4WithExcludedTag = contentRepository.save(buildContent(category, genre).categories(List.of(category, category3)).build());
        var content5WitDifferentCategory = contentRepository.save(buildContent(category2, genre).build());

        var parentContent = contentRepository.save(buildContent(category, genre).name(UUID.randomUUID() + text + UUID.randomUUID())
                                                                                .type(ContentType.SERIES)
                                                                                .structureType(ContentStructureType.GROUP)
                                                                                .build());
        contentGroupRepository.save(buildContentGroup(parentContent, content).sortOrder(1).build());
        contentGroupRepository.save(buildContentGroup(parentContent, content2).sortOrder(2).build());
        contentGroupRepository.save(buildContentGroup(parentContent, content3).sortOrder(3).disabledDate(LocalDateTime.now(clock)).build());
        contentGroupRepository.save(buildContentGroup(parentContent, content4WithExcludedTag).sortOrder(4).build());
        contentGroupRepository.save(buildContentGroup(parentContent, content5WitDifferentCategory).sortOrder(5).build());

        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(parentContent.getId().getId(), tag.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(parentContent.getId().getId(), tag2.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content.getId().getId(), tag.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content.getId().getId(), tag2.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content2.getId().getId(), tag.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content2.getId().getId(), tag2.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content3.getId().getId(), tag.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content4WithExcludedTag.getId().getId(), tag.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content4WithExcludedTag.getId().getId(), excludedTag.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content5WitDifferentCategory.getId().getId(), tag.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content5WitDifferentCategory.getId().getId(), tag2.getId().getId())).build());

        var contentSection = contentSectionsRepository.save(buildContentSection().numberOfElements(2).build());
        var contentSectionNextPage = contentSectionsRepository.save(buildContentSection().sortOrder(10).numberOfElements(2).build());
        var contentSectionCriteria = contentSectionCriteriaRepository.save(buildContentSectionCriteria(contentSection.getId()).relationType(AND)
                                                                                                                              .referenceIds(tag.getId() +
                                                                                                                                            "," +
                                                                                                                                            tag2.getId())
                                                                                                                              .build());
        var contentSectionCriteria2 = contentSectionCriteriaRepository.save(buildContentSectionCriteria(contentSection.getId()).type(ContentSectionCriteriaType.TAG)
                                                                                                                               .referenceIds(excludedTag.getId()
                                                                                                                                                        .toString())
                                                                                                                               .relationType(NOT)
                                                                                                                               .build());
        var contentSectionCriteria3 = contentSectionCriteriaRepository.save(buildContentSectionCriteria(contentSection.getId()).type(ContentSectionCriteriaType.CATEGORY)
                                                                                                                               .relationType(AND)
                                                                                                                               .referenceIds(category.getId()
                                                                                                                                                     .toString())
                                                                                                                               .build());

        var expectedContent3 = contentMapper.convertToDto(content3, Set.of(parentContent.getId().getId())).get();
        var expectedContent4 = contentMapper.convertToDto(parentContent, null).get();


        //when
        var result = webTestClient.get()
                                  .uri(uriBuilder -> uriBuilder.path(String.format("/sections/%s/main", DISCOVERY))
                                                               .queryParam("offset", 0)
                                                               .queryParam("limit", 1)
                                                               .build())
                                  .accept(MediaType.APPLICATION_JSON)
                                  .exchange()
                                  .expectStatus()
                                  .isOk()
                                  .expectBody(ContentSectionsDto.class)
                                  .returnResult()
                                  .getResponseBody();

        //then
        assertThat(result).isNotNull();
        assertThat(result.isHasNext()).isTrue();
        assertThat(result.getViewType()).isEqualTo(DISCOVERY);
        assertThat(result.getSections()).containsExactlyInAnyOrder(contentSectionMapper.buildContentSectionDto(contentSection,
                                                                                                               List.of(expectedContent4, expectedContent3),
                                                                                                               true,
                                                                                                               Set.of(tag.getId().getId(),
                                                                                                                      tag2.getId().getId()),
                                                                                                               Set.of(category.getId().getId()),
                                                                                                               Set.of(excludedTag.getId().getId()),
                                                                                                               Set.of(),
                                                                                                               0,
                                                                                                               contentSection.getNumberOfElements()));
    }

    @Test
    void shouldGetContentSectionsOnlyForExperts() {
        //given
        var text = UUID.randomUUID().toString();
        var categoryId = new CategoryId();
        var tagId = new TagId();

        var tag = tagsRepository.save(buildTag().id(tagId).build());
        var tag2 = tagsRepository.save(buildTag().build());
        var excludedTag = tagsRepository.save(buildTag().build());

        var category = categoriesRepository.save(buildCategory().id(categoryId).build());
        var genre = genresRepository.save(buildGenre().build());

        var content = contentRepository.save(buildContent(category, genre).build());
        var content2 = contentRepository.save(buildContent(category, genre).structureType(ContentStructureType.GROUP).type(ContentType.EXPERT).build());
        var content3 = contentRepository.save(buildContent(category, genre).build());

        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content.getId().getId(), tag.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content.getId().getId(), tag2.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content2.getId().getId(), tag.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content2.getId().getId(), tag2.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content3.getId().getId(), tag.getId().getId())).build());

        var contentSection = contentSectionsRepository.save(buildContentSection().contentType(ContentSectionType.EXPERTS).numberOfElements(2).build());

        var expectedContent = contentMapper.convertToDto(content2, null).get();


        //when
        var result = webTestClient.get()
                                  .uri(String.format("/sections/%s/main", DISCOVERY))
                                  .accept(MediaType.APPLICATION_JSON)
                                  .exchange()
                                  .expectStatus()
                                  .isOk()
                                  .expectBody(ContentSectionsDto.class)
                                  .returnResult()
                                  .getResponseBody();

        //then
        assertThat(result).isNotNull();
        assertThat(result.isHasNext()).isFalse();
        assertThat(result.getViewType()).isEqualTo(DISCOVERY);
        assertThat(result.getSections()).containsExactlyInAnyOrder(contentSectionMapper.buildContentSectionDto(contentSection,
                                                                                                               List.of(expectedContent),
                                                                                                               false,
                                                                                                               Set.of(),
                                                                                                               Set.of(),
                                                                                                               Set.of(),
                                                                                                               Set.of(),
                                                                                                               0,
                                                                                                               contentSection.getNumberOfElements()));
    }

    @Test
    void shouldGetContentSectionsOnlyForFeatured() {
        //given
        var text = UUID.randomUUID().toString();
        var categoryId = new CategoryId();
        var tagId = new TagId();

        var tag = tagsRepository.save(buildTag().id(tagId).build());
        var tag2 = tagsRepository.save(buildTag().build());
        var excludedTag = tagsRepository.save(buildTag().build());

        var category = categoriesRepository.save(buildCategory().id(categoryId).build());
        var genre = genresRepository.save(buildGenre().build());

        var content = contentRepository.save(buildContent(category, genre).featured(false).build());
        var content2 = contentRepository.save(buildContent(category, genre).featured(false)
                                                                           .structureType(ContentStructureType.GROUP)
                                                                           .type(ContentType.EXPERT)
                                                                           .build());
        var content3 = contentRepository.save(buildContent(category, genre).featured(true).build());

        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content.getId().getId(), tag.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content.getId().getId(), tag2.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content2.getId().getId(), tag.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content2.getId().getId(), tag2.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content3.getId().getId(), tag.getId().getId())).build());

        var contentSection = contentSectionsRepository.save(buildContentSection().contentType(ContentSectionType.HERO_CONTENT).numberOfElements(2).build());

        var expectedContent = contentMapper.convertToDto(content3, null).get();


        //when
        var result = webTestClient.get()
                                  .uri(String.format("/sections/%s/main", DISCOVERY))
                                  .accept(MediaType.APPLICATION_JSON)
                                  .exchange()
                                  .expectStatus()
                                  .isOk()
                                  .expectBody(ContentSectionsDto.class)
                                  .returnResult()
                                  .getResponseBody();

        //then
        assertThat(result).isNotNull();
        assertThat(result.isHasNext()).isFalse();
        assertThat(result.getViewType()).isEqualTo(DISCOVERY);
        assertThat(result.getSections()).containsExactlyInAnyOrder(contentSectionMapper.buildContentSectionDto(contentSection,
                                                                                                               List.of(expectedContent),
                                                                                                               false,
                                                                                                               Set.of(),
                                                                                                               Set.of(),
                                                                                                               Set.of(),
                                                                                                               Set.of(),
                                                                                                               0,
                                                                                                               contentSection.getNumberOfElements()));
    }


    @Test
    void shouldGetContentSectionsWithEverythingIfNoCriteriaFound() {
        //given
        var text = UUID.randomUUID().toString();
        var categoryId = new CategoryId();
        var tagId = new TagId();

        var tag = tagsRepository.save(buildTag().id(tagId).build());
        var tag2 = tagsRepository.save(buildTag().build());
        var excludedTag = tagsRepository.save(buildTag().build());

        var category = categoriesRepository.save(buildCategory().id(categoryId).build());
        var genre = genresRepository.save(buildGenre().build());

        var content = contentRepository.save(buildContent(category, genre).featured(false).build());
        var content2 = contentRepository.save(buildContent(category, genre).featured(false)
                                                                           .structureType(ContentStructureType.GROUP)
                                                                           .type(ContentType.EXPERT)
                                                                           .build());
        var content3 = contentRepository.save(buildContent(category, genre).featured(true).build());

        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content.getId().getId(), tag.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content.getId().getId(), tag2.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content2.getId().getId(), tag.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content2.getId().getId(), tag2.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content3.getId().getId(), tag.getId().getId())).build());

        var contentSection = contentSectionsRepository.save(buildContentSection().contentType(ContentSectionType.REGULAR).numberOfElements(10).build());

        var expectedContent = contentMapper.convertToDto(content3, null).get();
        var expectedContent2 = contentMapper.convertToDto(content2, null).get();
        var expectedContent3 = contentMapper.convertToDto(content, null).get();


        //when
        var result = webTestClient.get()
                                  .uri(String.format("/sections/%s/main", DISCOVERY))
                                  .accept(MediaType.APPLICATION_JSON)
                                  .exchange()
                                  .expectStatus()
                                  .isOk()
                                  .expectBody(ContentSectionsDto.class)
                                  .returnResult()
                                  .getResponseBody();

        //then
        assertThat(result).isNotNull();
        assertThat(result.isHasNext()).isFalse();
        assertThat(result.getViewType()).isEqualTo(DISCOVERY);
        assertThat(result.getSections()).containsExactlyInAnyOrder(contentSectionMapper.buildContentSectionDto(contentSection,
                                                                                                               List.of(expectedContent,
                                                                                                                       expectedContent2,
                                                                                                                       expectedContent3),
                                                                                                               false,
                                                                                                               Set.of(),
                                                                                                               Set.of(),
                                                                                                               Set.of(),
                                                                                                               Set.of(),
                                                                                                               0,
                                                                                                               contentSection.getNumberOfElements()));
    }

    @Test
    void shouldNotGetContentSectionsIfNotSupportedViewIsProvided() {
        //when
        var result = webTestClient.get()
                                  .uri("/sections/WRONG/main")
                                  .accept(MediaType.APPLICATION_JSON)
                                  .exchange()
                                  .expectStatus()
                                  .isBadRequest()
                                  .expectBody(ErrorResponse.class)
                                  .returnResult()
                                  .getResponseBody();

        //then
        assertThat(result).isNotNull();
        assertThat(result.message()).isEqualTo("viewType WRONG not supported");
    }


    @Test
    void shouldSearchContentByExplorerFilteredByGivenTagsAndCategories() {
        //given
        var text = UUID.randomUUID().toString();
        var categoryId = new CategoryId();
        var tagId = new TagId();

        var tag = tagsRepository.save(buildTag().id(tagId).build());
        var tag2 = tagsRepository.save(buildTag().build());
        var tagWithDifferentTextAndTag = tagsRepository.save(buildTag().build());

        var category = categoriesRepository.save(buildCategory().id(categoryId).build());
        var category2 = categoriesRepository.save(buildCategory().code(text + UUID.randomUUID()).build());
        var categoryDifferentTextAndCode = categoriesRepository.save(buildCategory().code(UUID.randomUUID().toString()).build());
        var genre = genresRepository.save(buildGenre().build());

        var content = contentRepository.save(buildContent(category, genre).build());
        var content2 = contentRepository.save(buildContent(category, genre).name(UUID.randomUUID() + text + UUID.randomUUID()).build());
        var content3 = contentRepository.save(buildContent(category2, genre).build());
        var content4 = contentRepository.save(buildContent(categoryDifferentTextAndCode, genre).build());
        var content5 = contentRepository.save(buildContent(categoryDifferentTextAndCode, genre).build());

        var parentContent = contentRepository.save(buildContent(categoryDifferentTextAndCode, genre).name(UUID.randomUUID() + text + UUID.randomUUID())
                                                                                                    .type(ContentType.SERIES)
                                                                                                    .structureType(ContentStructureType.GROUP)
                                                                                                    .build());
        contentGroupRepository.save(buildContentGroup(parentContent, content).sortOrder(1).build());
        contentGroupRepository.save(buildContentGroup(parentContent, content2).sortOrder(2).build());
        contentGroupRepository.save(buildContentGroup(parentContent, content3).sortOrder(3).disabledDate(LocalDateTime.now(clock)).build());
        contentGroupRepository.save(buildContentGroup(parentContent, content4).sortOrder(4).build());
        contentGroupRepository.save(buildContentGroup(parentContent, content5).sortOrder(5).build());

        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content2.getId().getId(), tag.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content5.getId().getId(), tag2.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content4.getId().getId(), tagWithDifferentTextAndTag.getId().getId())).build());

        var request = ExplorerDto.builder().tags(Set.of(tagId.getId())).categories(Set.of(categoryId.getId())).offset(0).limit(10).build();

        //when
        var result = webTestClient.post()
                                  .uri("/sections/explore")
                                  .accept(MediaType.APPLICATION_JSON)
                                  .contentType(MediaType.APPLICATION_JSON)
                                  .bodyValue(request)
                                  .exchange()
                                  .expectStatus()
                                  .isOk()
                                  .expectBody(ContentsDto.class)
                                  .returnResult()
                                  .getResponseBody();

        //then
        assertThat(result).isNotNull();
        assertThat(result.isHasNext()).isFalse();
        assertThat(result.getContents()).containsExactlyInAnyOrder(contentMapper.convertToDto(content2, Set.of(parentContent.getId().getId())).get());
    }

    @Test
    void shouldNotSearchContentFByExplorerIfInvalidLimitIsProvided() {
        //given
        var request = ExplorerDto.builder().offset(0).limit(0).build();

        //when
        webTestClient.post()
                     .uri("/sections/explore")
                     .accept(MediaType.APPLICATION_JSON)
                     .contentType(MediaType.APPLICATION_JSON)
                     .bodyValue(request)
                     .exchange()
                     .expectStatus()
                     .isBadRequest();
    }

}
