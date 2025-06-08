package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.IntegrationTest;
import com.umc.sp.contents.persistence.model.Category;
import com.umc.sp.contents.persistence.model.Content;
import com.umc.sp.contents.persistence.model.ContentGroup;
import com.umc.sp.contents.persistence.model.Genero;
import com.umc.sp.contents.persistence.model.Group;
import com.umc.sp.contents.persistence.model.id.CategoryId;
import com.umc.sp.contents.persistence.model.id.ContentGroupId;
import com.umc.sp.contents.persistence.model.id.ContentId;
import com.umc.sp.contents.persistence.model.id.GeneroId;
import com.umc.sp.contents.persistence.model.id.GroupId;
import com.umc.sp.contents.persistence.model.type.ContentType;
import com.umc.sp.contents.persistence.model.type.GroupType;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static com.umc.sp.contents.persistence.model.type.CategoryType.TOPIC;

public class ContentGroupRepositoryIntegrationTest implements IntegrationTest {

    @Autowired
    private ContentGroupRepository contentGroupRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GenerosRepository generosRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;


    @Autowired
    private Clock clock;

    @BeforeEach
    void setUp() {
        cleanUp();
    }

    @AfterEach
    void cleanUp() {
        contentGroupRepository.deleteAll();
        groupRepository.deleteAll();
        contentRepository.deleteAll();
        generosRepository.deleteAll();
        categoriesRepository.deleteAll();

    }

    @Test
    void shouldSaveAndFindById() {
        //given
        var category = categoriesRepository.save(Category.builder()
                                                         .id(new CategoryId())
                                                         .type(TOPIC)
                                                         .description(UUID.randomUUID().toString())
                                                         .code(UUID.randomUUID().toString())
                                                         .build());
        var genero = generosRepository.save(Genero.builder()
                                                  .id(new GeneroId())
                                                  .code(UUID.randomUUID().toString())
                                                  .description(UUID.randomUUID().toString())
                                                  .build());
        var group = groupRepository.save(Group.builder()
                                              .id(new GroupId())
                                              .type(GroupType.SERIES)
                                              .categoryId(new CategoryId())
                                              .featured(true)
                                              .name(UUID.randomUUID().toString())
                                              .description(UUID.randomUUID().toString())
                                              .genero(genero)
                                              .episodes(4)
                                              .duration("1:30:00")
                                              .build());

        var content = contentRepository.save(Content.builder()
                                                    .id(new ContentId())
                                                    .featured(true)
                                                    .type(ContentType.VIDEO)
                                                    .category(category)
                                                    .name(UUID.randomUUID().toString())
                                                    .description(UUID.randomUUID().toString())
                                                    .genero(genero)
                                                    .especialidadId(UUID.randomUUID())
                                                    .resourceUrl(UUID.randomUUID().toString())
                                                    .cdnUrl(UUID.randomUUID().toString())
                                                    .rating(BigDecimal.valueOf(4.5))
                                                    .duration("1:30:00")
                                                    .build());


        var contentGroup = ContentGroup.builder()
                                       .id(new ContentGroupId(content.getId().getId(), group.getId().getId()))
                                       .sortOrder(0)
                                       .disableDate(LocalDateTime.now(clock))
                                       .build();

        //when
        var saved = contentGroupRepository.save(contentGroup);
        var result = contentGroupRepository.findById(contentGroup.getId());

        //then
        Assertions.assertThat(result).get().isEqualTo(saved);
        Assertions.assertThat(result).get().isEqualTo(contentGroup);
    }
}
