package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.IntegrationTest;
import com.umc.sp.contents.persistence.model.Tag;
import com.umc.sp.contents.persistence.model.id.TagId;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TagsRepositoryIntegrationTest implements IntegrationTest {

    @Autowired
    private TagsRepository tagsRepository;

    @BeforeEach
    void setUp() {
        cleanUp();
    }

    @AfterEach
    void cleanUp() {
        tagsRepository.deleteAll();
    }

    @Test
    void shouldSaveAndFindById() {
        //given
        var tag = Tag.builder().id(new TagId()).code(UUID.randomUUID().toString()).description(UUID.randomUUID().toString()).build();

        //when
        var saved = tagsRepository.save(tag);
        var result = tagsRepository.findById(tag.getId());

        //then
        Assertions.assertThat(result).get().isEqualTo(saved);
        Assertions.assertThat(result).get().isEqualTo(tag);
    }
}
