package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.persistence.model.SubscriptionPlan;
import com.umc.sp.contents.persistence.model.id.SubscriptionPlanId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionPlansRepository extends JpaRepository<SubscriptionPlan, SubscriptionPlanId> {
}
