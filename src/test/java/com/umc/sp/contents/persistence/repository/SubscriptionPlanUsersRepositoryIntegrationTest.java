package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.IntegrationTest;
import com.umc.sp.contents.persistence.model.SubscriptionPlan;
import com.umc.sp.contents.persistence.model.SubscriptionPlanUser;
import com.umc.sp.contents.persistence.model.id.CountryCode;
import com.umc.sp.contents.persistence.model.id.CurrencyCode;
import com.umc.sp.contents.persistence.model.id.SubscriptionPlanId;
import com.umc.sp.contents.persistence.model.id.SubscriptionPlanUserId;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.*;

public class SubscriptionPlanUsersRepositoryIntegrationTest implements IntegrationTest {

    @Autowired
    private SubscriptionPlanUsersRepository subscriptionPlanUsersRepository;

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
        subscriptionPlanUsersRepository.deleteAll();
        subscriptionPlansRepository.deleteAll();
    }

    @Test
    void shouldSaveAndFindById() {
        //given
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

        var subscriptionPlanUser = SubscriptionPlanUser.builder()
                                                       .id(new SubscriptionPlanUserId(subscriptionPlan.getId().getId(), UUID.randomUUID()))
                                                       .disableDate(LocalDateTime.now(clock))
                                                       .build();

        //when
        var saved = subscriptionPlanUsersRepository.save(subscriptionPlanUser);
        var result = subscriptionPlanUsersRepository.findById(subscriptionPlanUser.getId());

        //then
        assertThat(result).get().isEqualTo(saved);
        assertThat(result).get().isEqualTo(subscriptionPlanUser);
    }
}