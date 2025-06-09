package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.IntegrationTest;
import com.umc.sp.contents.persistence.model.ContentSection;
import com.umc.sp.contents.persistence.model.id.ContentSectionId;
import com.umc.sp.contents.persistence.model.type.ContentSectionSortType;
import com.umc.sp.contents.persistence.model.type.ContentSectionType;
import java.time.Clock;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ContentSectionsRepositoryIntegrationTest implements IntegrationTest {

    @Autowired
    private ContentSectionsRepository contentSectionsRepository;

    @Autowired
    private Clock clock;

    @BeforeEach
    void setUp() {
        cleanUp();
    }

    @AfterEach
    void cleanUp() {
        contentSectionsRepository.deleteAll();
    }

    @Test
    void shouldSaveAndFindById() {
        //given
        var contentSection = ContentSection.builder()
                                           .id(new ContentSectionId())
                                           .type(ContentSectionType.FEATURED)
                                           .title(UUID.randomUUID().toString())
                                           .titleCode(UUID.randomUUID().toString())
                                           .numberOfElements(4)
                                           .sortType(ContentSectionSortType.PRIORITY)
                                           .build();

        //when
        var saved = contentSectionsRepository.save(contentSection);
        var result = contentSectionsRepository.findById(contentSection.getId());

        //then
        Assertions.assertThat(result).get().usingRecursiveComparison().isEqualTo(saved);
        Assertions.assertThat(result).get().usingRecursiveComparison().isEqualTo(contentSection);
    }
}
