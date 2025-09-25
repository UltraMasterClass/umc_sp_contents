package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.IntegrationTest;
import java.time.Clock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.*;
import static utils.ObjectTestUtils.buildGenre;

public class GenreRepositoryIntegrationTest implements IntegrationTest {

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
        genresRepository.deleteAll();
    }

    @Test
    void shouldSaveAndFindById() {
        //given
        var genero = buildGenre().build();

        //when
        var saved = genresRepository.save(genero);
        var result = genresRepository.findById(genero.getId());

        //then
        assertThat(result).get().isEqualTo(saved);
        assertThat(result).get().isEqualTo(genero);
    }
}
