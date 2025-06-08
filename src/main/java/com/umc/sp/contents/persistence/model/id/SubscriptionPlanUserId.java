package com.umc.sp.contents.persistence.model.id;

import java.io.Serializable;
import java.util.UUID;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class SubscriptionPlanUserId implements Serializable {
    private UUID subscriptionPlanId;
    private UUID userId;

    public SubscriptionPlanUserId() {
    }

    public SubscriptionPlanUserId(UUID subscriptionPlanId, UUID userId) {
        this.subscriptionPlanId = subscriptionPlanId;
        this.userId = userId;
    }
}
