package com.umc.sp.contents.controller;

import com.umc.sp.contents.IntegrationTest;
import com.umc.sp.contents.dto.response.ContentsDto;
import com.umc.sp.contents.dto.response.ExplorerDto;
import com.umc.sp.contents.mapper.ContentMapper;
import com.umc.sp.contents.persistence.model.ContentTag;
import com.umc.sp.contents.persistence.model.id.ContentTagId;
import com.umc.sp.contents.persistence.repository.*;
import java.time.Clock;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.assertj.core.api.Assertions.assertThat;
import static utils.ObjectTestUtils.*;

public class ContentSectionExplorerExclusionTest implements IntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

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
    private ContentMapper contentMapper;

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
    void shouldExcludeContentWithSpecificTagsInExplorer() {
        //given
        var tagUltrasonido = tagsRepository.save(buildTag().code("ULTRASONIDO").build());
        var tagEquipoMedico = tagsRepository.save(buildTag().code("EQUIPO_MEDICO").build());
        var tagOtro = tagsRepository.save(buildTag().code("OTRO").build());

        var category = categoriesRepository.save(buildCategory().build());
        var genre = genresRepository.save(buildGenre().build());

        // Contenido que debe aparecer (solo tiene tag Ultrasonido)
        var contentGood1 = contentRepository.save(buildContent(category, genre).name("Good Content 1").build());
        var contentGood2 = contentRepository.save(buildContent(category, genre).name("Good Content 2").build());
        
        // Contenido que NO debe aparecer (Voluson Expert con tag Equipo Médico)
        var voluson1 = contentRepository.save(buildContent(category, genre).name("Voluson Expert 1").build());
        var voluson2 = contentRepository.save(buildContent(category, genre).name("Voluson Expert 2").build());
        var voluson3 = contentRepository.save(buildContent(category, genre).name("Voluson Expert 3").build());

        // Asociar tags
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(contentGood1.getId().getId(), tagUltrasonido.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(contentGood2.getId().getId(), tagUltrasonido.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(contentGood2.getId().getId(), tagOtro.getId().getId())).build());
        
        // Voluson tienen ambos tags: Ultrasonido Y Equipo Médico
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(voluson1.getId().getId(), tagUltrasonido.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(voluson1.getId().getId(), tagEquipoMedico.getId().getId())).build());
        
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(voluson2.getId().getId(), tagUltrasonido.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(voluson2.getId().getId(), tagEquipoMedico.getId().getId())).build());
        
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(voluson3.getId().getId(), tagUltrasonido.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(voluson3.getId().getId(), tagEquipoMedico.getId().getId())).build());

        var request = ExplorerDto.builder()
                .tags(Set.of(tagUltrasonido.getId().getId()))
                .excludeTags(Set.of(tagEquipoMedico.getId().getId()))
                .offset(0)
                .limit(10)
                .build();

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
        assertThat(result.getContents()).hasSize(2);
        
        // Verificar que solo están los contenidos buenos
        var contentNames = result.getContents().stream()
                .map(content -> content.getName())
                .toList();
        
        assertThat(contentNames).containsExactlyInAnyOrder("Good Content 1", "Good Content 2");
        assertThat(contentNames).doesNotContain("Voluson Expert 1", "Voluson Expert 2", "Voluson Expert 3");
    }

    @Test
    void shouldWorkWithMultipleExclusionsInExplorer() {
        //given
        var tagUltrasonido = tagsRepository.save(buildTag().code("ULTRASONIDO").build());
        var tagEquipoMedico = tagsRepository.save(buildTag().code("EQUIPO_MEDICO").build());
        var tagDeprecated = tagsRepository.save(buildTag().code("DEPRECATED").build());

        var category = categoriesRepository.save(buildCategory().build());
        var categoryExcluded = categoriesRepository.save(buildCategory().code("EXCLUDED_CAT").build());
        var genre = genresRepository.save(buildGenre().build());

        // Contenido que debe aparecer
        var contentGood = contentRepository.save(buildContent(category, genre).name("Good Content").build());
        
        // Contenido que NO debe aparecer por tag excluido
        var contentBadTag = contentRepository.save(buildContent(category, genre).name("Bad Tag Content").build());
        
        // Contenido que NO debe aparecer por categoría excluida
        var contentBadCategory = contentRepository.save(buildContent(categoryExcluded, genre).name("Bad Category Content").build());

        // Asociar tags
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(contentGood.getId().getId(), tagUltrasonido.getId().getId())).build());
        
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(contentBadTag.getId().getId(), tagUltrasonido.getId().getId())).build());
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(contentBadTag.getId().getId(), tagEquipoMedico.getId().getId())).build());
        
        contentTagRepository.save(ContentTag.builder().id(new ContentTagId(contentBadCategory.getId().getId(), tagUltrasonido.getId().getId())).build());

        var request = ExplorerDto.builder()
                .tags(Set.of(tagUltrasonido.getId().getId()))
                .excludeTags(Set.of(tagEquipoMedico.getId().getId(), tagDeprecated.getId().getId()))
                .excludeCategories(Set.of(categoryExcluded.getId().getId()))
                .offset(0)
                .limit(10)
                .build();

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
        assertThat(result.getContents()).hasSize(1);
        assertThat(result.getContents().get(0).getName()).isEqualTo("Good Content");
    }
}
