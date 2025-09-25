package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.IntegrationTest;
import java.time.Clock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static com.umc.sp.contents.persistence.model.type.ContentSectionCriteriaRelationType.*;
import static java.time.LocalDateTime.*;
import static org.assertj.core.api.Assertions.assertThat;
import static utils.ObjectTestUtils.buildContentSection;
import static utils.ObjectTestUtils.buildContentSectionCriteria;

public class ContentSectionCriteriaRepositoryIntegrationTest implements IntegrationTest {

    @Autowired
    private ContentSectionCriteriaRepository contentSectionCriteriaRepository;

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
        contentSectionCriteriaRepository.deleteAll();
        contentSectionsRepository.deleteAll();
    }


    @Test
    void shouldSaveAndFindById() {
        //given
        var contentSection = contentSectionsRepository.save(buildContentSection().disabledDate(now(clock)).build());
        var contentSectionCriteria = buildContentSectionCriteria(contentSection.getId()).disabledDate(now(clock)).build();

        //when
        var saved = contentSectionCriteriaRepository.save(contentSectionCriteria);
        var result = contentSectionCriteriaRepository.findById(contentSectionCriteria.getId());

        //then
        assertThat(result).get().usingRecursiveComparison().isEqualTo(saved);
        assertThat(result).get().usingRecursiveComparison().isEqualTo(contentSectionCriteria);
    }

    @Test
    void shouldFindByContentSectionIdAndDisabledDateIsNull() {
        //given
        var contentSection = contentSectionsRepository.save(buildContentSection().disabledDate(now(clock)).build());
        var contentSectionCriteria = contentSectionCriteriaRepository.save(buildContentSectionCriteria(contentSection.getId()).relationType(AND)
                                                                                                                              .disabledDate(now(clock))
                                                                                                                              .build());
        var contentSectionCriteria2 = contentSectionCriteriaRepository.save(buildContentSectionCriteria(contentSection.getId()).relationType(NOT).build());

        //when
        var result = contentSectionCriteriaRepository.findByContentSectionIdAndDisabledDateIsNull(contentSection.getId());

        //then
        assertThat(result).containsExactlyInAnyOrder(contentSectionCriteria2);
    }
}
