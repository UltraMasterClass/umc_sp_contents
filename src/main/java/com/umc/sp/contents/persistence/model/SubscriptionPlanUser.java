package com.umc.sp.contents.persistence.model;

import com.umc.sp.contents.persistence.model.id.SubscriptionPlanUserId;
import jakarta.persistence.Column;
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
@Table(name = "subscription_plan_users")
public class SubscriptionPlanUser {

    @EmbeddedId
    private SubscriptionPlanUserId id;

    @Column(name = "disable_date")
    private LocalDateTime disableDate;

}
