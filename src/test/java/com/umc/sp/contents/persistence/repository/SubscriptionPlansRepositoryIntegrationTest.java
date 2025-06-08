package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.IntegrationTest;
import com.umc.sp.contents.persistence.model.SubscriptionPlan;
import com.umc.sp.contents.persistence.model.id.CountryCode;
import com.umc.sp.contents.persistence.model.id.CurrencyCode;
import com.umc.sp.contents.persistence.model.id.SubscriptionPlanId;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SubscriptionPlansRepositoryIntegrationTest implements IntegrationTest {

    @Autowired
    private SubscriptionPlansRepository subscriptionPlansRepository;

    @Autowired
    private Clock clock;

    @BeforeEach
    void setUp() {
        cleanUp();
    }

    @AfterEach
    void cleanUp() {
        subscriptionPlansRepository.deleteAll();
    }

    @Test
    void shouldSaveAndFindById() {
        //given
        var subscriptionPlan = SubscriptionPlan.builder()
                                               .id(new SubscriptionPlanId())
                                               .name(UUID.randomUUID().toString())
                                               .description(UUID.randomUUID().toString())
                                               .price(BigDecimal.valueOf(50.55))
                                               .vatPercentage(BigDecimal.valueOf(15.55))
                                               .currencyCode(new CurrencyCode())
                                               .countryCode(new CountryCode())
                                               .disableDate(LocalDateTime.now(clock))
                                               .build();

        //when
        var saved = subscriptionPlansRepository.save(subscriptionPlan);
        var result = subscriptionPlansRepository.findById(subscriptionPlan.getId());

        //then
        Assertions.assertThat(result).get().isEqualTo(saved);
        Assertions.assertThat(result).get().isEqualTo(subscriptionPlan);
    }
}
