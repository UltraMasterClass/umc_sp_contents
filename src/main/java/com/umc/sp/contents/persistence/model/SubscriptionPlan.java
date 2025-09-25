package com.umc.sp.contents.persistence.model;

import com.umc.sp.contents.persistence.model.id.CountryCode;
import com.umc.sp.contents.persistence.model.id.CurrencyCode;
import com.umc.sp.contents.persistence.model.id.SubscriptionPlanId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "subscription_plan")
public class SubscriptionPlan {

    @EmbeddedId
    @AttributeOverride(name = "id", column = @Column(name = "id"))
    private SubscriptionPlanId id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "vat_percentage", nullable = false)
    private BigDecimal vatPercentage;

    @Column(name = "currency_code", nullable = false, length = 5)
    @Convert(converter = CurrencyCode.DbConverter.class)
    private CurrencyCode currencyCode;

    @Column(name = "country_code", nullable = false, length = 5)
    @Convert(converter = CountryCode.DbConverter.class)
    private CountryCode countryCode;

    @Column(name = "disable_date")
    private LocalDateTime disableDate;
}
