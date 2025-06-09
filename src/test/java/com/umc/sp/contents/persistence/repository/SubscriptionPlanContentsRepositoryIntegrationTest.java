package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.IntegrationTest;
import com.umc.sp.contents.persistence.model.Category;
import com.umc.sp.contents.persistence.model.Content;
import com.umc.sp.contents.persistence.model.Genders;
import com.umc.sp.contents.persistence.model.SubscriptionPlan;
import com.umc.sp.contents.persistence.model.SubscriptionPlanContent;
import com.umc.sp.contents.persistence.model.id.CategoryId;
import com.umc.sp.contents.persistence.model.id.ContentId;
import com.umc.sp.contents.persistence.model.id.CountryCode;
import com.umc.sp.contents.persistence.model.id.CurrencyCode;
import com.umc.sp.contents.persistence.model.id.GenderId;
import com.umc.sp.contents.persistence.model.id.SubscriptionPlanContentId;
import com.umc.sp.contents.persistence.model.id.SubscriptionPlanId;
import com.umc.sp.contents.persistence.model.type.ContentType;
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
    private GendersRepository gendersRepository;

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
        gendersRepository.deleteAll();
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
        var genero = gendersRepository.save(Genders.builder()
                                                   .id(new GenderId())
                                                   .code(UUID.randomUUID().toString())
                                                   .description(UUID.randomUUID().toString())
                                                   .build());


        var content = contentRepository.save(Content.builder()
                                                    .id(new ContentId())
                                                    .featured(true)
                                                    .type(ContentType.VIDEO)
                                                    .category(category)
                                                    .name(UUID.randomUUID().toString())
                                                    .description(UUID.randomUUID().toString())
                                                    .genders(genero)
                                                    .especialidadId(UUID.randomUUID())
                                                    .resourceUrl(UUID.randomUUID().toString())
                                                    .cdnUrl(UUID.randomUUID().toString())
                                                    .rating(BigDecimal.valueOf(4.5))
                                                    .duration("1:30:00")
                                                    .build());

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
        Assertions.assertThat(result).get().usingRecursiveComparison().isEqualTo(saved);
        Assertions.assertThat(result).get().usingRecursiveComparison().isEqualTo(subscriptionPlanContent);
    }

}