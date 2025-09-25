package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.IntegrationTest;
import java.time.Clock;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import static com.umc.sp.contents.persistence.model.ContentSection.SORT_ORDER_FIELD;
import static com.umc.sp.contents.persistence.model.type.ContentSectionViewType.DISCOVERY;
import static org.assertj.core.api.Assertions.*;
import static utils.ObjectTestUtils.buildContentSection;

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
        var contentSection = buildContentSection().disabledDate(LocalDateTime.now(clock)).build();

        //when
        var saved = contentSectionsRepository.save(contentSection);
        var result = contentSectionsRepository.findById(contentSection.getId());

        //then
        assertThat(result).get().usingRecursiveComparison().isEqualTo(saved);
        assertThat(result).get().usingRecursiveComparison().isEqualTo(contentSection);
    }


    @Test
    void shouldFindByViewTypeAndDisabledDateIsNull() {
        //given
        var contentSection = contentSectionsRepository.save(buildContentSection().sortOrder(0).build());
        var contentSectionDeleted = contentSectionsRepository.save(buildContentSection().sortOrder(1).disabledDate(LocalDateTime.now(clock)).build());
        var contentSection2 = contentSectionsRepository.save(buildContentSection().sortOrder(2).build());
        var contentSection3 = contentSectionsRepository.save(buildContentSection().sortOrder(3).build());

        //when
        var result = contentSectionsRepository.findByViewTypeAndDisabledDateIsNull(DISCOVERY,
                                                                                   PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, SORT_ORDER_FIELD)));

        //then
        assertThat(result.hasNext()).isTrue();
        assertThat(result).usingRecursiveFieldByFieldElementComparator().containsExactly(contentSection, contentSection2);
    }
}
