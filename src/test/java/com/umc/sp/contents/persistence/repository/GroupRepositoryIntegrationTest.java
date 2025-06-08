package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.IntegrationTest;
import com.umc.sp.contents.persistence.model.Genero;
import com.umc.sp.contents.persistence.model.Group;
import com.umc.sp.contents.persistence.model.id.CategoryId;
import com.umc.sp.contents.persistence.model.id.GeneroId;
import com.umc.sp.contents.persistence.model.id.GroupId;
import com.umc.sp.contents.persistence.model.type.GroupType;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class GroupRepositoryIntegrationTest implements IntegrationTest {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GenerosRepository generosRepository;

    @BeforeEach
    void setUp() {
        cleanUp();
    }

    @AfterEach
    void cleanUp() {
        groupRepository.deleteAll();
        generosRepository.deleteAll();
    }

    @Test
    void shouldSaveAndFindById() {
        //given
        var genero = generosRepository.save(Genero.builder()
                                                  .id(new GeneroId())
                                                  .code(UUID.randomUUID().toString())
                                                  .description(UUID.randomUUID().toString())
                                                  .build());
        var group = Group.builder()
                         .id(new GroupId())
                         .type(GroupType.SERIES)
                         .categoryId(new CategoryId())
                         .featured(true)
                         .name(UUID.randomUUID().toString())
                         .description(UUID.randomUUID().toString())
                         .genero(genero)
                         .episodes(4)
                         .duration("1:30:00")
                         .build();

        //when
        var saved = groupRepository.save(group);
        var result = groupRepository.findById(group.getId());

        //then
        Assertions.assertThat(result).get().isEqualTo(saved);
        Assertions.assertThat(result).get().isEqualTo(group);
    }
}