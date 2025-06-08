package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.IntegrationTest;
import com.umc.sp.contents.persistence.model.Genero;
import com.umc.sp.contents.persistence.model.id.GeneroId;
import java.time.Clock;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class GenerosRepositoryIntegrationTest implements IntegrationTest {

    @Autowired
    private GenerosRepository generosRepository;

    @Autowired
    private Clock clock;

    @BeforeEach
    void setUp() {
        cleanUp();
    }

    @AfterEach
    void cleanUp() {
        generosRepository.deleteAll();
    }

    @Test
    void shouldSaveAndFindById() {
        //given
        var genero = Genero.builder().id(new GeneroId()).code(UUID.randomUUID().toString()).description(UUID.randomUUID().toString()).build();

        //when
        var saved = generosRepository.save(genero);
        var result = generosRepository.findById(genero.getId());

        //then
        Assertions.assertThat(result).get().isEqualTo(saved);
        Assertions.assertThat(result).get().isEqualTo(genero);
    }
}
