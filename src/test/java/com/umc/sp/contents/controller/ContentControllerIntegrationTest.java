package com.umc.sp.contents.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.sp.contents.IntegrationTest;
import com.umc.sp.contents.dto.request.CreateContentDto;
import com.umc.sp.contents.dto.request.CreateContentInfoDto;
import com.umc.sp.contents.dto.response.ContentDetailDto;
import com.umc.sp.contents.dto.response.ContentInfoDto;
import com.umc.sp.contents.dto.response.ContentResourcesDto;
import com.umc.sp.contents.dto.response.ContentSeriesDetailDto;
import com.umc.sp.contents.dto.response.ContentsDto;
import com.umc.sp.contents.exception.ErrorResponse;
import com.umc.sp.contents.mapper.ContentMapper;
import com.umc.sp.contents.persistence.model.ContentTag;
import com.umc.sp.contents.persistence.model.Tag;
import com.umc.sp.contents.persistence.model.TagTranslation;
import com.umc.sp.contents.persistence.model.id.CategoryId;
import com.umc.sp.contents.persistence.model.id.ContentId;
import com.umc.sp.contents.persistence.model.id.ContentTagId;
import com.umc.sp.contents.persistence.model.id.TagId;
import com.umc.sp.contents.persistence.model.id.TagTranslationId;
import com.umc.sp.contents.persistence.model.type.ContentInfoType;
import com.umc.sp.contents.persistence.model.type.ContentStructureType;
import com.umc.sp.contents.persistence.model.type.ContentType;
import com.umc.sp.contents.persistence.repository.CategoriesRepository;
import com.umc.sp.contents.persistence.repository.ContentGroupRepository;
import com.umc.sp.contents.persistence.repository.ContentInfoRepository;
import com.umc.sp.contents.persistence.repository.ContentRepository;
import com.umc.sp.contents.persistence.repository.ContentTagRepository;
import com.umc.sp.contents.persistence.repository.GenresRepository;
import com.umc.sp.contents.persistence.repository.TagTranslationsRepository;
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
import static com.umc.sp.contents.persistence.model.type.ContentInfoType.LARGE_COVER_IMG_URL;
import static com.umc.sp.contents.persistence.model.type.ContentInfoType.LOGO_IMG_URL;
import static com.umc.sp.contents.persistence.model.type.ContentInfoType.RESOURCE_URL;
import static com.umc.sp.contents.persistence.model.type.ContentInfoType.SMALL_COVER_IMG_URL;
import static com.umc.sp.contents.persistence.model.type.ContentInfoType.THUMBNAIL_IMG_URL;
import static com.umc.sp.contents.persistence.model.type.ContentStructureType.*;
import static com.umc.sp.contents.persistence.model.type.ContentType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static utils.ObjectTestUtils.buildCategory;
import static utils.ObjectTestUtils.buildContent;
import static utils.ObjectTestUtils.buildContentGroup;
import static utils.ObjectTestUtils.buildContentInfo;
import static utils.ObjectTestUtils.buildGenre;
import static utils.ObjectTestUtils.buildTag;

public class ContentControllerIntegrationTest implements IntegrationTest {

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
    private TagTranslationsRepository tagTranslationsRepository;

    @Autowired
    private ContentMapper contentMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Clock clock;

    @BeforeEach
    void setUp() {
        cleanUp();
    }

    @AfterEach
    void cleanUp() {
        tagTranslationsRepository.deleteAll();
        contentGroupRepository.deleteAll();
        contentInfoRepository.deleteAll();
        contentTagRepository.deleteAll();
        contentRepository.deleteAll();
        genresRepository.deleteAll();
        categoriesRepository.deleteAll();
        tagsRepository.deleteAll();
    }


    @Test
    void shouldGetContentById() {
        //given
        var tag = tagsRepository.save(buildTag().build());
        var category = categoriesRepository.save(buildCategory().build());
        var category2 = categoriesRepository.save(buildCategory().build());
        var genre = genresRepository.save(buildGenre().build());
        var parentContent = contentRepository.save(buildContent(category, genre).type(EXPERT).structureType(GROUP).build());
        var parentContent2 = contentRepository.save(buildContent(category, genre).type(EXPERT).structureType(GROUP).build());
        var contentInfo = buildContentInfo().build();
        var content = buildContent(category, genre).structureType(INDIVIDUAL)
                                                   .contentInfos(List.of(contentInfo))
                                                   .categories(List.of(category, category2))
                                                   .build();
        contentInfo.setContent(content);
        content = contentRepository.save(content);
        var contentGroup = contentGroupRepository.save(buildContentGroup(parentContent, content).build());
        var contentGroup2 = contentGroupRepository.save(buildContentGroup(parentContent2, content).build());
        var contentTag = contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content.getId().getId(), tag.getId().getId())).build());

        //when
        var result = webTestClient.get()
                                  .uri("/content/{contentId}", content.getId())
                                  .accept(MediaType.APPLICATION_JSON)
                                  .exchange()
                                  .expectStatus()
                                  .isOk()
                                  .expectBody(ContentDetailDto.class)
                                  .returnResult()
                                  .getResponseBody();

        //then
        assertThat(result).isEqualTo(contentMapper.convertToDetailDto(content,
                                                                      Set.of(parentContent.getId().getId(), parentContent2.getId().getId()),
                                                                      List.of(tag)).get());
    }

    @Test
    void shouldGetContentSeriesDetailById() {
        //given
        var tag = tagsRepository.save(buildTag().build());
        var category = categoriesRepository.save(buildCategory().build());
        var category2 = categoriesRepository.save(buildCategory().build());
        var genre = genresRepository.save(buildGenre().build());
        var parentContent = contentRepository.save(buildContent(category, genre).type(SERIES).structureType(GROUP).build());
        var contentInfo = buildContentInfo().build();
        var content = buildContent(category, genre).structureType(EPISODE)
                                                   .contentInfos(List.of(contentInfo))
                                                   .categories(List.of(category, category2))
                                                   .build();
        contentInfo.setContent(content);
        content = contentRepository.save(content);
        var contentGroup = contentGroupRepository.save(buildContentGroup(parentContent, content).build());
        var contentTag = contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content.getId().getId(), tag.getId().getId())).build());

        var expectedResult = (ContentSeriesDetailDto) contentMapper.convertToDetailDto(parentContent, Set.of(), List.of(tag)).get();
        expectedResult.setEpisodes(1);

        //when
        var result = webTestClient.get()
                                  .uri("/content/{contentId}", parentContent.getId())
                                  .accept(MediaType.APPLICATION_JSON)
                                  .exchange()
                                  .expectStatus()
                                  .isOk()
                                  .expectBody(ContentSeriesDetailDto.class)
                                  .returnResult()
                                  .getResponseBody();

        //then
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void shouldGetErrorIFContentByIdNotFound() {
        //given
        var contentId = new ContentId();

        //when
        var result = webTestClient.get()
                                  .uri("/content/{contentId}", contentId)
                                  .accept(MediaType.APPLICATION_JSON)
                                  .exchange()
                                  .expectStatus()
                                  .isNotFound()
                                  .expectBody(ErrorResponse.class)
                                  .returnResult()
                                  .getResponseBody();

        //then
        assertThat(result).isEqualTo(new ErrorResponse(String.format("Content with id %s not found", contentId)));
    }

    @Test
    void shouldGetContentByParentId() {
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

        var expectedResult = ContentsDto.builder()
                                        .contents(List.of(contentMapper.convertToDto(content, Set.of(parentContent.getId().getId())).get(),
                                                          contentMapper.convertToDto(content2, Set.of(parentContent.getId().getId())).get(),
                                                          contentMapper.convertToDto(content4, Set.of(parentContent.getId().getId())).get()))
                                        .hasNext(true)
                                        .build();

        //when
        var result = webTestClient.get()
                                  .uri(uriBuilder -> uriBuilder.path(String.format("/content/%s/children", parentContent.getId()))
                                                               .queryParam("offset", 0)
                                                               .queryParam("limit", 3)
                                                               .build())
                                  .accept(MediaType.APPLICATION_JSON)
                                  .exchange()
                                  .expectStatus()
                                  .isOk()
                                  .expectBody(ContentsDto.class)
                                  .returnResult()
                                  .getResponseBody();

        //then
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void shouldSearchContent() {
        //given
        var text = UUID.randomUUID().toString();
        var tag = tagsRepository.save(buildTag().code(text + UUID.randomUUID()).build());
        var tag2 = tagsRepository.save(buildTag().code(UUID.randomUUID() + text).build());
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
        // Las siguientes categorías tienen código con el texto pero NO serán encontradas
        var category = categoriesRepository.save(buildCategory().code(UUID.randomUUID() + text).build());
        var category2 = categoriesRepository.save(buildCategory().code(text + UUID.randomUUID()).build());
        var categoryDifferentText = categoriesRepository.save(buildCategory().code(UUID.randomUUID().toString()).build());

        var genre = genresRepository.save(buildGenre().build());

        var contentInfo = buildContentInfo().build();
        var content = buildContent(category, genre).name("bbbbbbbb").contentInfos(List.of(contentInfo)).build();
        contentInfo.setContent(content);
        content = contentRepository.save(content);
        // content2 y content3 NO serán encontrados porque la búsqueda por category.code está desactivada
        var content2 = contentRepository.save(buildContent(category, genre).name("ccccccccc").build());
        // excluded from results - NO será encontrado
        var content3 = contentRepository.save(buildContent(category2, genre).name("zzzzzzzz").build());
        // excluded tag, category and title do not contain the text to search
        var content4 = contentRepository.save(buildContent(categoryDifferentText, genre).name("111aaaaaaaa").build());
        var content5 = contentRepository.save(buildContent(categoryDifferentText, genre).name("dddddddd").build());

        // parentContent NO será encontrado porque la búsqueda por content.name está desactivada
        var parentContent = contentRepository.save(buildContent(categoryDifferentText, genre).name("aaaaaaaaa" + text + UUID.randomUUID())
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

        // ACTUALIZADO: Solo esperamos content (por tag) y content5 (por tag)
        // parentContent NO se encuentra porque la búsqueda por content.name está desactivada
        // content2 y content3 NO se encuentran porque la búsqueda por category.code está desactivada
        var expectedResponse = objectMapper.valueToTree(ContentsDto.builder()
                                                                   .contents(List.of(contentMapper.convertToDto(content, Set.of(parentContent.getId().getId()))
                                                                                                  .get(),
                                                                                     contentMapper.convertToDto(content5, Set.of(parentContent.getId().getId()))
                                                                                                  .get()))
                                                                   .hasNext(false)
                                                                   .build());

        //when
        var result = webTestClient.get()
                                  .uri(uriBuilder -> uriBuilder.path("/content/search")
                                                               .queryParam("text", text)
                                                               .queryParam("offset", 0)
                                                               .queryParam("limit", 3)
                                                               .build())
                                  .accept(MediaType.APPLICATION_JSON)
                                  .exchange()
                                  .expectStatus()
                                  .isOk()
                                  .expectBody(JsonNode.class)
                                  .returnResult()
                                  .getResponseBody();

        //then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedResponse);
    }


    @Test
    void shouldSearchContentFilteredByTagAndCategory() {
        //given
        var text = UUID.randomUUID().toString();
        var categoryId = new CategoryId();
        var tagId = new TagId();

        var tag = tagsRepository.save(buildTag().id(tagId).build());
        var tag2 = tagsRepository.save(buildTag().build());
        var tagWithDifferentTextAndTag = tagsRepository.save(buildTag().build());

        // IMPORTANTE: tagTranslation debe contener el texto para que content2 sea encontrado
        var tagTranslation = TagTranslation.builder().id(new TagTranslationId(tag.getId(), "es")).value(text + UUID.randomUUID()).tag(tag).build();
        var tagTranslation2 = TagTranslation.builder().id(new TagTranslationId(tag2.getId(), "es")).value(UUID.randomUUID() + text).tag(tag2).build();
        var tagTranslationDifferent = TagTranslation.builder()
                                                    .id(new TagTranslationId(tagWithDifferentTextAndTag.getId(), "es"))
                                                    .value(UUID.randomUUID().toString())
                                                    .tag(tagWithDifferentTextAndTag)
                                                    .build();

        tagTranslationsRepository.saveAll(List.of(tagTranslation, tagTranslation2, tagTranslationDifferent));

        var category = categoriesRepository.save(buildCategory().id(categoryId).build());
        var category2 = categoriesRepository.save(buildCategory().code(text + UUID.randomUUID()).build());
        var categoryDifferentTextAndCode = categoriesRepository.save(buildCategory().code(UUID.randomUUID().toString()).build());
        var genre = genresRepository.save(buildGenre().build());

        var content = contentRepository.save(buildContent(category, genre).build());
        // content2 NO tiene el texto en el nombre porque la búsqueda por name está desactivada
        var content2 = contentRepository.save(buildContent(category, genre).name(UUID.randomUUID().toString()).build());
        var content3 = contentRepository.save(buildContent(category2, genre).build());
        var content4 = contentRepository.save(buildContent(categoryDifferentTextAndCode, genre).build());
        var content5 = contentRepository.save(buildContent(categoryDifferentTextAndCode, genre).build());

        // parentContent tampoco tiene el texto en el nombre porque la búsqueda por name está desactivada
        var parentContent = contentRepository.save(buildContent(categoryDifferentTextAndCode, genre).name(UUID.randomUUID().toString())
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

        //when
        var result = webTestClient.get()
                                  .uri(uriBuilder -> uriBuilder.path("/content/search")
                                                               .queryParam("text", text)
                                                               .queryParam("tags", Set.of(tagId))
                                                               .queryParam("categories", Set.of(categoryId.getId()))
                                                               .build())
                                  .accept(MediaType.APPLICATION_JSON)
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
    void shouldCreateExpertContent() {
        //given
        var tag = tagsRepository.save(buildTag().build());
        var category = categoriesRepository.save(buildCategory().build());
        var category2 = categoriesRepository.save(buildCategory().build());
        var request = CreateContentDto.builder()
                                      .name(UUID.randomUUID().toString())
                                      .description(UUID.randomUUID().toString())
                                      .type(EXPERT)
                                      .structureType(GROUP)
                                      .categories(Set.of(category.getId().getId(), category2.getId().getId()))
                                      .attributes(Set.of(CreateContentInfoDto.builder()
                                                                             .type(ContentInfoType.EXPERTS)
                                                                             .value(UUID.randomUUID().toString())
                                                                             .build()))
                                      .tags(Set.of(tag.getId().getId()))
                                      .build();

        //when
        var result = webTestClient.post()
                                  .uri("/content")
                                  .accept(MediaType.APPLICATION_JSON)
                                  .contentType(MediaType.APPLICATION_JSON)
                                  .bodyValue(request)
                                  .exchange()
                                  .expectStatus()
                                  .isOk()
                                  .expectBody(ContentResourcesDto.class)
                                  .returnResult()
                                  .getResponseBody();

        //then
        var contents = contentRepository.findAll();
        assertThat(contents).hasSize(1);
        var createdContent = contents.get(0);
        assertThat(result.getId()).isEqualTo(createdContent.getId().getId());
        assertThat(result.getResources()).usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                                         .containsExactlyInAnyOrder(ContentInfoDto.builder()
                                                                                  .type(SMALL_COVER_IMG_URL)
                                                                                  .value(String.format("https://test/assets/experts/%s/images/profile_image.jpg",
                                                                                                       createdContent.getId()))
                                                                                  .build());
    }


    @Test
    void shouldCreateSeriesContent() {
        //given
        var tag = tagsRepository.save(buildTag().build());
        var category = categoriesRepository.save(buildCategory().build());
        var category2 = categoriesRepository.save(buildCategory().build());
        var request = CreateContentDto.builder()
                                      .name(UUID.randomUUID().toString())
                                      .description(UUID.randomUUID().toString())
                                      .type(SERIES)
                                      .structureType(GROUP)
                                      .categories(Set.of(category.getId().getId(), category2.getId().getId()))
                                      .attributes(Set.of(CreateContentInfoDto.builder()
                                                                             .type(ContentInfoType.EXPERTS)
                                                                             .value(UUID.randomUUID().toString())
                                                                             .build()))
                                      .tags(Set.of(tag.getId().getId()))
                                      .build();

        //when
        var result = webTestClient.post()
                                  .uri("/content")
                                  .accept(MediaType.APPLICATION_JSON)
                                  .contentType(MediaType.APPLICATION_JSON)
                                  .bodyValue(request)
                                  .exchange()
                                  .expectStatus()
                                  .isOk()
                                  .expectBody(ContentResourcesDto.class)
                                  .returnResult()
                                  .getResponseBody();

        //then
        var contents = contentRepository.findAll();
        assertThat(contents).hasSize(1);
        var createdContent = contents.get(0);
        assertThat(result.getId()).isEqualTo(createdContent.getId().getId());
        assertThat(result.getResources()).usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                                         .containsExactlyInAnyOrder(ContentInfoDto.builder()
                                                                                  .type(LOGO_IMG_URL)
                                                                                  .value(String.format("https://test/assets/series/%s/images/logo.svg",
                                                                                                       createdContent.getId()))
                                                                                  .build(),
                                                                    ContentInfoDto.builder()
                                                                                  .type(THUMBNAIL_IMG_URL)
                                                                                  .value(String.format("https://test/assets/series/%s/images/thumbnail.jpg",
                                                                                                       createdContent.getId()))
                                                                                  .build(),
                                                                    ContentInfoDto.builder()
                                                                                  .type(SMALL_COVER_IMG_URL)
                                                                                  .value(String.format("https://test/assets/series/%s/images/small_cover.jpg",
                                                                                                       createdContent.getId()))
                                                                                  .build(),

                                                                    ContentInfoDto.builder()
                                                                                  .type(LARGE_COVER_IMG_URL)
                                                                                  .value(String.format("https://test/assets/series/%s/images/large_cover.jpg",
                                                                                                       createdContent.getId()))
                                                                                  .build());
    }


    @Test
    void shouldCreateVideoContent() {
        //given
        var tag = tagsRepository.save(buildTag().build());
        var category = categoriesRepository.save(buildCategory().build());
        var category2 = categoriesRepository.save(buildCategory().build());
        var request = CreateContentDto.builder()
                                      .name(UUID.randomUUID().toString())
                                      .description(UUID.randomUUID().toString())
                                      .type(VIDEO)
                                      .structureType(INDIVIDUAL)
                                      .categories(Set.of(category.getId().getId(), category2.getId().getId()))
                                      .attributes(Set.of(CreateContentInfoDto.builder()
                                                                             .type(ContentInfoType.EXPERTS)
                                                                             .value(UUID.randomUUID().toString())
                                                                             .build()))
                                      .tags(Set.of(tag.getId().getId()))
                                      .build();

        //when
        var result = webTestClient.post()
                                  .uri("/content")
                                  .accept(MediaType.APPLICATION_JSON)
                                  .contentType(MediaType.APPLICATION_JSON)
                                  .bodyValue(request)
                                  .exchange()
                                  .expectStatus()
                                  .isOk()
                                  .expectBody(ContentResourcesDto.class)
                                  .returnResult()
                                  .getResponseBody();

        //then
        var contents = contentRepository.findAll();
        assertThat(contents).hasSize(1);
        var createdContent = contents.get(0);
        assertThat(result.getId()).isEqualTo(createdContent.getId().getId());
        assertThat(result.getResources()).usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                                         .containsExactlyInAnyOrder(ContentInfoDto.builder()
                                                                                  .type(RESOURCE_URL)
                                                                                  .value(String.format("https://test/content/videos/%s/",
                                                                                                       createdContent.getId()))
                                                                                  .build(),
                                                                    ContentInfoDto.builder()
                                                                                  .type(THUMBNAIL_IMG_URL)
                                                                                  .value(String.format("https://test/assets/videos/%s/images/thumbnail.jpg",
                                                                                                       createdContent.getId()))
                                                                                  .build(),
                                                                    ContentInfoDto.builder()
                                                                                  .type(SMALL_COVER_IMG_URL)
                                                                                  .value(String.format("https://test/assets/videos/%s/images/small_cover.jpg",
                                                                                                       createdContent.getId()))
                                                                                  .build(),

                                                                    ContentInfoDto.builder()
                                                                                  .type(LARGE_COVER_IMG_URL)
                                                                                  .value(String.format("https://test/assets/videos/%s/images/large_cover.jpg",
                                                                                                       createdContent.getId()))
                                                                                  .build());
        assertThat(tagsRepository.findAll()).usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                                            .containsExactlyInAnyOrder(tag,
                                                                       Tag.builder()
                                                                          .code(createdContent.getName())
                                                                          .description(createdContent.getDescription())
                                                                          .build());

        assertThat(tagTranslationsRepository.findAll()).usingRecursiveFieldByFieldElementComparatorIgnoringFields("id", "tag")
                                                       .containsExactlyInAnyOrder(TagTranslation.builder().value(createdContent.getName()).build());
    }


    @Test
    void shouldNotCreateContentIfGivenCategoriesAreHierarchicallyRelated() {
        //given
        var tag = tagsRepository.save(buildTag().build());
        var parentCategory = categoriesRepository.save(buildCategory().build());
        var category = categoriesRepository.save(buildCategory().parent(parentCategory).build());
        var category2 = categoriesRepository.save(buildCategory().build());
        var request = CreateContentDto.builder()
                                      .name(UUID.randomUUID().toString())
                                      .description(UUID.randomUUID().toString())
                                      .type(VIDEO)
                                      .structureType(INDIVIDUAL)
                                      .categories(Set.of(category.getId().getId(), category2.getId().getId(), parentCategory.getId().getId()))
                                      .attributes(Set.of(CreateContentInfoDto.builder()
                                                                             .type(ContentInfoType.EXPERTS)
                                                                             .value(UUID.randomUUID().toString())
                                                                             .build()))
                                      .tags(Set.of(tag.getId().getId()))
                                      .build();

        //when
        var result = webTestClient.post()
                                  .uri("/content")
                                  .accept(MediaType.APPLICATION_JSON)
                                  .contentType(MediaType.APPLICATION_JSON)
                                  .bodyValue(request)
                                  .exchange()
                                  .expectStatus()
                                  .isEqualTo(CONFLICT)
                                  .expectBody(ErrorResponse.class)
                                  .returnResult()
                                  .getResponseBody();

        //then
        assertThat(result).isEqualTo(new ErrorResponse("Given categories must not be parents of each other"));
        var contents = contentRepository.findAll();
        assertThat(contents).isEmpty();
    }

    @Test
    void shouldNotCreateContentIfGivenParentsAreHierarchicallyRelated() {
        //given
        var tag = tagsRepository.save(buildTag().build());
        var category = categoriesRepository.save(buildCategory().build());
        var category2 = categoriesRepository.save(buildCategory().build());

        var genre = genresRepository.save(buildGenre().build());
        var parentContent = contentRepository.save(buildContent(category, genre).type(EXPERT).structureType(GROUP).build());
        var content = contentRepository.save(buildContent(category, genre).structureType(INDIVIDUAL).build());
        contentGroupRepository.save(buildContentGroup(parentContent, content).build());

        var request = CreateContentDto.builder()
                                      .name(UUID.randomUUID().toString())
                                      .description(UUID.randomUUID().toString())
                                      .type(VIDEO)
                                      .structureType(EPISODE)
                                      .categories(Set.of(category.getId().getId(), category2.getId().getId()))
                                      .attributes(Set.of(CreateContentInfoDto.builder()
                                                                             .type(ContentInfoType.EXPERTS)
                                                                             .value(UUID.randomUUID().toString())
                                                                             .build()))
                                      .parentContents(Set.of(parentContent.getId().getId(), content.getId().getId()))
                                      .tags(Set.of(tag.getId().getId()))
                                      .build();

        //when
        var result = webTestClient.post()
                                  .uri("/content")
                                  .accept(MediaType.APPLICATION_JSON)
                                  .contentType(MediaType.APPLICATION_JSON)
                                  .bodyValue(request)
                                  .exchange()
                                  .expectStatus()
                                  .isEqualTo(CONFLICT)
                                  .expectBody(ErrorResponse.class)
                                  .returnResult()
                                  .getResponseBody();

        //then
        assertThat(result).isEqualTo(new ErrorResponse("Given parent contents must not be parents of each other"));
        var contents = contentRepository.findAll();
        assertThat(contents).hasSize(2);
    }


    @Test
    void shouldNotCreateContentIfGivenTypesAreNotCompatible() {
        //given
        var tag = tagsRepository.save(buildTag().build());
        var parentCategory = categoriesRepository.save(buildCategory().build());
        var category = categoriesRepository.save(buildCategory().parent(parentCategory).build());
        var request = CreateContentDto.builder()
                                      .name(UUID.randomUUID().toString())
                                      .description(UUID.randomUUID().toString())
                                      .type(VIDEO)
                                      .structureType(GROUP)
                                      .categories(Set.of(category.getId().getId()))
                                      .attributes(Set.of(CreateContentInfoDto.builder()
                                                                             .type(ContentInfoType.EXPERTS)
                                                                             .value(UUID.randomUUID().toString())
                                                                             .build()))
                                      .tags(Set.of(tag.getId().getId()))
                                      .build();

        //when
        var result = webTestClient.post()
                                  .uri("/content")
                                  .accept(MediaType.APPLICATION_JSON)
                                  .contentType(MediaType.APPLICATION_JSON)
                                  .bodyValue(request)
                                  .exchange()
                                  .expectStatus()
                                  .isEqualTo(BAD_REQUEST)
                                  .expectBody(ErrorResponse.class)
                                  .returnResult()
                                  .getResponseBody();

        //then
        assertThat(result).isEqualTo(new ErrorResponse("GROUP can not be of type VIDEO"));
        var contents = contentRepository.findAll();
        assertThat(contents).isEmpty();
    }

    @Test
    void shouldNotCreateContentIfGivenParentIsNotGroupingType() {
        //given
        var tag = tagsRepository.save(buildTag().build());
        var parentCategory = categoriesRepository.save(buildCategory().build());
        var category = categoriesRepository.save(buildCategory().parent(parentCategory).build());
        var content = contentRepository.save(buildContent(category, null).type(VIDEO).structureType(EPISODE).build());
        var request = CreateContentDto.builder()
                                      .name(UUID.randomUUID().toString())
                                      .description(UUID.randomUUID().toString())
                                      .type(VIDEO)
                                      .structureType(EPISODE)
                                      .categories(Set.of(category.getId().getId()))
                                      .attributes(Set.of(CreateContentInfoDto.builder()
                                                                             .type(ContentInfoType.EXPERTS)
                                                                             .value(UUID.randomUUID().toString())
                                                                             .build()))
                                      .parentContents(Set.of(content.getId().getId()))
                                      .tags(Set.of(tag.getId().getId()))
                                      .build();

        //when
        var result = webTestClient.post()
                                  .uri("/content")
                                  .accept(MediaType.APPLICATION_JSON)
                                  .contentType(MediaType.APPLICATION_JSON)
                                  .bodyValue(request)
                                  .exchange()
                                  .expectStatus()
                                  .isEqualTo(BAD_REQUEST)
                                  .expectBody(ErrorResponse.class)
                                  .returnResult()
                                  .getResponseBody();

        //then
        assertThat(result).isEqualTo(new ErrorResponse("EPISODE should not be parent"));
        var contents = contentRepository.findAll();
        assertThat(contents).hasSize(1);
    }


    @Test
    void shouldNotCreateContentIfParentNotGivenAndTypeIsNonParent() {
        //given
        var tag = tagsRepository.save(buildTag().build());
        var parentCategory = categoriesRepository.save(buildCategory().build());
        var category = categoriesRepository.save(buildCategory().parent(parentCategory).build());
        var request = CreateContentDto.builder()
                                      .name(UUID.randomUUID().toString())
                                      .description(UUID.randomUUID().toString())
                                      .type(VIDEO)
                                      .structureType(EPISODE)
                                      .categories(Set.of(category.getId().getId()))
                                      .attributes(Set.of(CreateContentInfoDto.builder()
                                                                             .type(ContentInfoType.EXPERTS)
                                                                             .value(UUID.randomUUID().toString())
                                                                             .build()))
                                      .tags(Set.of(tag.getId().getId()))
                                      .build();

        //when
        var result = webTestClient.post()
                                  .uri("/content")
                                  .accept(MediaType.APPLICATION_JSON)
                                  .contentType(MediaType.APPLICATION_JSON)
                                  .bodyValue(request)
                                  .exchange()
                                  .expectStatus()
                                  .isEqualTo(BAD_REQUEST)
                                  .expectBody(ErrorResponse.class)
                                  .returnResult()
                                  .getResponseBody();

        //then
        assertThat(result).isEqualTo(new ErrorResponse("EPISODE should have parent"));
        var contents = contentRepository.findAll();
        assertThat(contents).isEmpty();
    }

    @Test
    void shouldNotCreateContentIfGivenTypeIsExpertAndAParentIsGiven() {
        //given
        var tag = tagsRepository.save(buildTag().build());
        var parentCategory = categoriesRepository.save(buildCategory().build());
        var category = categoriesRepository.save(buildCategory().parent(parentCategory).build());
        var content = contentRepository.save(buildContent(category, null).type(EXPERT).structureType(GROUP).build());
        var request = CreateContentDto.builder()
                                      .name(UUID.randomUUID().toString())
                                      .description(UUID.randomUUID().toString())
                                      .type(EXPERT)
                                      .structureType(GROUP)
                                      .categories(Set.of(category.getId().getId()))
                                      .attributes(Set.of(CreateContentInfoDto.builder()
                                                                             .type(ContentInfoType.EXPERTS)
                                                                             .value(UUID.randomUUID().toString())
                                                                             .build()))
                                      .parentContents(Set.of(content.getId().getId()))
                                      .tags(Set.of(tag.getId().getId()))
                                      .build();

        //when
        var result = webTestClient.post()
                                  .uri("/content")
                                  .accept(MediaType.APPLICATION_JSON)
                                  .contentType(MediaType.APPLICATION_JSON)
                                  .bodyValue(request)
                                  .exchange()
                                  .expectStatus()
                                  .isEqualTo(BAD_REQUEST)
                                  .expectBody(ErrorResponse.class)
                                  .returnResult()
                                  .getResponseBody();

        //then
        assertThat(result).isEqualTo(new ErrorResponse("EXPERTS should not have parent"));
        var contents = contentRepository.findAll();
        assertThat(contents).hasSize(1);
    }

}
