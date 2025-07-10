package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.IntegrationTest;
import com.umc.sp.contents.persistence.model.Subtitle;
import com.umc.sp.contents.persistence.model.id.LanguageCode;
import com.umc.sp.contents.persistence.model.id.SubtitleId;
import java.time.Clock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.*;
import static utils.ObjectTestUtils.buildCategory;
import static utils.ObjectTestUtils.buildContent;
import static utils.ObjectTestUtils.buildGenre;

public class SubtitlesRepositoryIntegrationTest implements IntegrationTest {

    @Autowired
    private SubtitlesRepository subtitlesRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private GenresRepository genresRepository;

    @Autowired
    private Clock clock;


    @BeforeEach
    void setUp() {
        cleanUp();
    }

    @AfterEach
    void cleanUp() {
        subtitlesRepository.deleteAll();
        contentRepository.deleteAll();
        genresRepository.deleteAll();
        categoriesRepository.deleteAll();
    }

    @Test
    void shouldSaveAndFindById() {
        //given
        var category = categoriesRepository.save(buildCategory().build());
        var genre = genresRepository.save(buildGenre().build());
        var content = contentRepository.save(buildContent(category, genre).build());

        var subtitle = Subtitle.builder().id(new SubtitleId()).contentId(content.getId()).priority(1).languageCode(new LanguageCode()).build();

        //when
        var saved = subtitlesRepository.save(subtitle);
        var result = subtitlesRepository.findById(subtitle.getId());

        //then
        assertThat(result).get().isEqualTo(saved);
        assertThat(result).get().isEqualTo(subtitle);
    }
}

