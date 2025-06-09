package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.persistence.model.SubscriptionPlanContent;
import com.umc.sp.contents.persistence.model.id.SubscriptionPlanContentId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionPlanContentsRepository extends JpaRepository<SubscriptionPlanContent, SubscriptionPlanContentId> {
}
