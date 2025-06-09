package com.umc.sp.contents.persistence.model;

import com.umc.sp.contents.persistence.model.id.ContentId;
import com.umc.sp.contents.persistence.model.id.SubscriptionPlanContentId;
import com.umc.sp.contents.persistence.model.id.SubscriptionPlanId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
@Table(name = "subscription_plan_content")
public class SubscriptionPlanContent {

    @EmbeddedId
    @AttributeOverride(name = "id", column = @Column(name = "id"))
    private SubscriptionPlanContentId id;

    @Column(name = "subscription_plan_id", nullable = false)
    @Convert(converter = SubscriptionPlanId.DbConverter.class)
    private SubscriptionPlanId subscriptionPlanId;

    @Column(name = "content_id", nullable = false)
    @Convert(converter = ContentId.DbConverter.class)
    private ContentId contentId;

    @Column(name = "disable_date")
    private LocalDateTime disableDate;

}
