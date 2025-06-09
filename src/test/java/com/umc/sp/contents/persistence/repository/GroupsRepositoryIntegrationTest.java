package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.IntegrationTest;
import com.umc.sp.contents.persistence.model.Genders;
import com.umc.sp.contents.persistence.model.Group;
import com.umc.sp.contents.persistence.model.id.CategoryId;
import com.umc.sp.contents.persistence.model.id.GenderId;
import com.umc.sp.contents.persistence.model.id.GroupId;
import com.umc.sp.contents.persistence.model.type.GroupType;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class GroupsRepositoryIntegrationTest implements IntegrationTest {

    @Autowired
    private GroupsRepository groupsRepository;

    @Autowired
    private GendersRepository gendersRepository;

    @BeforeEach
    void setUp() {
        cleanUp();
    }

    @AfterEach
    void cleanUp() {
        groupsRepository.deleteAll();
        gendersRepository.deleteAll();
    }

    @Test
    void shouldSaveAndFindById() {
        //given
        var genero = gendersRepository.save(Genders.builder()
                                                   .id(new GenderId())
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
                         .genders(genero)
                         .episodes(4)
                         .duration("1:30:00")
                         .build();

        //when
        var saved = groupsRepository.save(group);
        var result = groupsRepository.findById(group.getId());

        //then
        Assertions.assertThat(result).get().isEqualTo(saved);
        Assertions.assertThat(result).get().isEqualTo(group);
    }
}