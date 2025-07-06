package com.umc.sp.contents.controller;

import com.umc.sp.contents.IntegrationTest;
import com.umc.sp.contents.controller.dto.response.ContentDetailDto;
import com.umc.sp.contents.controller.dto.response.ContentsDto;
import com.umc.sp.contents.converter.ContentConverter;
import com.umc.sp.contents.persistence.model.ContentTag;
import com.umc.sp.contents.persistence.model.id.ContentTagId;
import com.umc.sp.contents.persistence.model.type.ContentStructureType;
import com.umc.sp.contents.persistence.model.type.ContentType;
import com.umc.sp.contents.persistence.repository.CategoriesRepository;
import com.umc.sp.contents.persistence.repository.ContentGroupRepository;
import com.umc.sp.contents.persistence.repository.ContentInfoRepository;
import com.umc.sp.contents.persistence.repository.ContentRepository;
import com.umc.sp.contents.persistence.repository.ContentTagRepository;
import com.umc.sp.contents.persistence.repository.GenresRepository;
import com.umc.sp.contents.persistence.repository.TagsRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import static com.umc.sp.contents.persistence.model.type.ContentStructureType.*;
import static com.umc.sp.contents.persistence.model.type.ContentType.*;
import static org.assertj.core.api.Assertions.assertThat;
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
    private ContentConverter contentConverter;

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
        contentTagRepository.deleteAll();
        contentRepository.deleteAll();
        genresRepository.deleteAll();
        categoriesRepository.deleteAll();
        tagsRepository.deleteAll();
    }


    @Test
    void shouldGetContentById() throws JSONException {
        //given
        var tag = tagsRepository.save(buildTag().build());
        var category = categoriesRepository.save(buildCategory().build());
        var genre = genresRepository.save(buildGenre().build());
        var parentContent = contentRepository.save(buildContent(category, genre).type(SERIES).structureType(GROUP).build());
        var contentInfo = buildContentInfo().build();
        var content = buildContent(category, genre).contentInfos(List.of(contentInfo)).build();
        contentInfo.setContent(content);
        content = contentRepository.save(content);
        var contentGroup = contentGroupRepository.save(buildContentGroup(parentContent, content).build());
        var contentTag = contentTagRepository.save(ContentTag.builder().id(new ContentTagId(content.getId().getId(), tag.getId().getId())).build());

        //when
        var result = webTestClient.get()
                                  .uri("/api/content/{contentId}", content.getId())
                                  .accept(MediaType.APPLICATION_JSON)
                                  .exchange()
                                  .expectStatus()
                                  .isOk()
                                  .expectBody(ContentDetailDto.class)
                                  .returnResult()
                                  .getResponseBody();

        //then
        assertThat(result).isEqualTo(contentConverter.convertToDetailDto(content, parentContent.getId().getId(), List.of(tag)));
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
                                        .contents(List.of(contentConverter.convertToDto(content, parentContent.getId().getId()),
                                                          contentConverter.convertToDto(content2, parentContent.getId().getId()),
                                                          contentConverter.convertToDto(content4, parentContent.getId().getId())))
                                        .hasNext(true)
                                        .build();

        //when
        var result = webTestClient.get()
                                  .uri(uriBuilder -> uriBuilder.path(String.format("/api/content/%s/content", parentContent.getId()))
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

}
