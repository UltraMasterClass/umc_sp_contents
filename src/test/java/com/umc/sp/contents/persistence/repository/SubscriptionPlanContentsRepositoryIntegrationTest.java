package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.IntegrationTest;
import com.umc.sp.contents.persistence.model.SubscriptionPlan;
import com.umc.sp.contents.persistence.model.SubscriptionPlanContent;
import com.umc.sp.contents.persistence.model.id.CountryCode;
import com.umc.sp.contents.persistence.model.id.CurrencyCode;
import com.umc.sp.contents.persistence.model.id.SubscriptionPlanContentId;
import com.umc.sp.contents.persistence.model.id.SubscriptionPlanId;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.*;
import static utils.ObjectTestUtils.buildCategory;
import static utils.ObjectTestUtils.buildContent;
import static utils.ObjectTestUtils.buildGenre;

public class SubscriptionPlanContentsRepositoryIntegrationTest implements IntegrationTest {

    @Autowired
    private SubscriptionPlanContentsRepository subscriptionPlanContentsRepository;

    @Autowired
    private SubscriptionPlansRepository subscriptionPlansRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private GenresRepository genresRepository;

    @Autowired
    private Clock clock;

    @BeforeEach
    void setUp() {
        cleanUp();
    }

    @AfterEach
    void cleanUp() {
        subscriptionPlanContentsRepository.deleteAll();
        subscriptionPlansRepository.deleteAll();
        contentRepository.deleteAll();
        genresRepository.deleteAll();
        categoriesRepository.deleteAll();
    }

    @Test
    void shouldSaveAndFindById() {
        //given
        var category = categoriesRepository.save(buildCategory().build());
        var genre = genresRepository.save(buildGenre().build());
        var content = contentRepository.save(buildContent(category, genre).build());

        var subscriptionPlan = subscriptionPlansRepository.save(SubscriptionPlan.builder()
                                                                                .id(new SubscriptionPlanId())
                                                                                .name(UUID.randomUUID().toString())
                                                                                .description(UUID.randomUUID().toString())
                                                                                .price(BigDecimal.valueOf(50.55))
                                                                                .vatPercentage(BigDecimal.valueOf(15.55))
                                                                                .currencyCode(new CurrencyCode())
                                                                                .countryCode(new CountryCode())
                                                                                .disableDate(LocalDateTime.now(clock))
                                                                                .build());

        var subscriptionPlanContent = SubscriptionPlanContent.builder()
                                                             .id(new SubscriptionPlanContentId())
                                                             .subscriptionPlanId(subscriptionPlan.getId())
                                                             .contentId(content.getId())
                                                             .disableDate(LocalDateTime.now(clock))
                                                             .build();

        //when
        var saved = subscriptionPlanContentsRepository.save(subscriptionPlanContent);
        var result = subscriptionPlanContentsRepository.findById(subscriptionPlanContent.getId());

        //then
        assertThat(result).get().usingRecursiveComparison().isEqualTo(saved);
        assertThat(result).get().usingRecursiveComparison().isEqualTo(subscriptionPlanContent);
    }

}