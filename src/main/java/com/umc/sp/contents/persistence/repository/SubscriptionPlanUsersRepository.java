package com.umc.sp.contents.persistence.repository;

import com.umc.sp.contents.persistence.model.SubscriptionPlanUser;
import com.umc.sp.contents.persistence.model.id.SubscriptionPlanUserId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionPlanUsersRepository extends JpaRepository<SubscriptionPlanUser, SubscriptionPlanUserId> {
}
