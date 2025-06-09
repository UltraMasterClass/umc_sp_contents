package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.IntegrationTest;
import com.umc.sp.contents.persistence.model.Genders;
import com.umc.sp.contents.persistence.model.id.GenderId;
import java.time.Clock;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class GendersRepositoryIntegrationTest implements IntegrationTest {

    @Autowired
    private GendersRepository gendersRepository;

    @Autowired
    private Clock clock;

    @BeforeEach
    void setUp() {
        cleanUp();
    }

    @AfterEach
    void cleanUp() {
        gendersRepository.deleteAll();
    }

    @Test
    void shouldSaveAndFindById() {
        //given
        var genero = Genders.builder().id(new GenderId()).code(UUID.randomUUID().toString()).description(UUID.randomUUID().toString()).build();

        //when
        var saved = gendersRepository.save(genero);
        var result = gendersRepository.findById(genero.getId());

        //then
        Assertions.assertThat(result).get().isEqualTo(saved);
        Assertions.assertThat(result).get().isEqualTo(genero);
    }
}
