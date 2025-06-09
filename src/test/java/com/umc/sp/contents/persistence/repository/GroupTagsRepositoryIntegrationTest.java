package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.IntegrationTest;
import com.umc.sp.contents.persistence.model.Genders;
import com.umc.sp.contents.persistence.model.Group;
import com.umc.sp.contents.persistence.model.GroupTag;
import com.umc.sp.contents.persistence.model.Tag;
import com.umc.sp.contents.persistence.model.id.CategoryId;
import com.umc.sp.contents.persistence.model.id.GenderId;
import com.umc.sp.contents.persistence.model.id.GroupId;
import com.umc.sp.contents.persistence.model.id.GroupTagId;
import com.umc.sp.contents.persistence.model.id.TagId;
import com.umc.sp.contents.persistence.model.type.GroupType;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class GroupTagsRepositoryIntegrationTest implements IntegrationTest {

    @Autowired
    private GroupTagsRepository groupTagsRepository;

    @Autowired
    private GroupsRepository groupsRepository;

    @Autowired
    private GendersRepository gendersRepository;

    @Autowired
    private TagsRepository tagsRepository;

    @Autowired
    private Clock clock;

    @BeforeEach
    void setUp() {
        cleanUp();
    }

    @AfterEach
    void cleanUp() {
        groupTagsRepository.deleteAll();
        groupsRepository.deleteAll();
        gendersRepository.deleteAll();
        tagsRepository.deleteAll();
    }

    @Test
    void shouldSaveAndFindById() {
        //given
        var tag = tagsRepository.save(Tag.builder().id(new TagId()).code(UUID.randomUUID().toString()).description(UUID.randomUUID().toString()).build());
        var genero = gendersRepository.save(Genders.builder()
                                                   .id(new GenderId())
                                                   .code(UUID.randomUUID().toString())
                                                   .description(UUID.randomUUID().toString())
                                                   .build());
        var group = groupsRepository.save(Group.builder()
                                               .id(new GroupId())
                                               .type(GroupType.SERIES)
                                               .categoryId(new CategoryId())
                                               .featured(true)
                                               .name(UUID.randomUUID().toString())
                                               .description(UUID.randomUUID().toString())
                                               .genders(genero)
                                               .episodes(4)
                                               .duration("1:30:00")
                                               .build());

        var groupTag = GroupTag.builder().id(new GroupTagId(group.getId().getId(), tag.getId().getId())).disableDate(LocalDateTime.now(clock)).build();


        //when
        var saved = groupTagsRepository.save(groupTag);
        var result = groupTagsRepository.findById(groupTag.getId());

        //then
        Assertions.assertThat(result).get().isEqualTo(saved);
        Assertions.assertThat(result).get().isEqualTo(groupTag);
    }

}