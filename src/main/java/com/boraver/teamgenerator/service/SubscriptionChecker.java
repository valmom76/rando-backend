package com.boraver.teamgenerator.service;

import com.boraver.teamgenerator.common.TenantContext;
import com.boraver.teamgenerator.entity.Subscription.SubscriptionStatus;
import com.boraver.teamgenerator.repository.SubscriptionRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SubscriptionChecker {

  private final SubscriptionRepository subscriptionRepo;

  public SubscriptionChecker(SubscriptionRepository subscriptionRepo) {
    this.subscriptionRepo = subscriptionRepo;
  }

  public boolean isActive() {
    String tenantIdStr = TenantContext.getTenantId();
    if (tenantIdStr == null) return false;
    UUID tenantId = UUID.fromString(tenantIdStr);
    return subscriptionRepo.existsByTenantIdAndStatus(tenantId, SubscriptionStatus.ACTIVE);
  }
}