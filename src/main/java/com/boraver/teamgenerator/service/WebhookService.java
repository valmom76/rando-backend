package com.boraver.teamgenerator.service;

import com.boraver.teamgenerator.entity.Plan;
import com.boraver.teamgenerator.entity.Subscription;
import com.boraver.teamgenerator.entity.Subscription.SubscriptionStatus;
import com.boraver.teamgenerator.repository.PlanRepository;
import com.boraver.teamgenerator.repository.SubscriptionRepository;
import com.boraver.teamgenerator.repository.TenantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Service
public class WebhookService {

  private final SubscriptionRepository subscriptionRepo;
  private final PlanRepository planRepo;
  private final TenantRepository tenantRepo;

  public WebhookService(SubscriptionRepository subscriptionRepo,
                        PlanRepository planRepo,
                        TenantRepository tenantRepo) {
    this.subscriptionRepo = subscriptionRepo;
    this.planRepo = planRepo;
    this.tenantRepo = tenantRepo;
  }

  @Transactional
  public void processAsaasEvent(Map<String, Object> payload) {
    String event = (String) payload.get("event");
    Map<String, Object> payment = (Map<String, Object>) payload.get("payment");

    if (payment != null) {
      String asaasSubId = (String) payment.get("subscription");
      Subscription sub = subscriptionRepo.findByAsaasSubscriptionId(asaasSubId);
      if (sub == null) return;

      switch (event) {
        case "PAYMENT_CONFIRMED" -> {
          sub.setStatus(SubscriptionStatus.ACTIVE);
          sub.setStartDate(LocalDate.now());
          subscriptionRepo.save(sub);
        }
        case "PAYMENT_OVERDUE" -> {
          sub.setStatus(SubscriptionStatus.SUSPENDED);
          subscriptionRepo.save(sub);
        }
        case "PAYMENT_DELETED" -> {
          sub.setStatus(SubscriptionStatus.CANCELLED);
          subscriptionRepo.save(sub);
          createFreeSubscription(sub.getTenant().getId());
        }
      }
    } else if ("SUBSCRIPTION_CANCELED".equals(event)) {
      Map<String, Object> subData = (Map<String, Object>) payload.get("subscription");
      String asaasSubId = (String) subData.get("id");
      Subscription sub = subscriptionRepo.findByAsaasSubscriptionId(asaasSubId);
      if (sub != null) {
        UUID tenantId = sub.getTenant().getId();
        sub.setStatus(SubscriptionStatus.CANCELLED);
        subscriptionRepo.save(sub);
        createFreeSubscription(tenantId);
      }
    }
  }

  private void createFreeSubscription(UUID tenantId) {
    Plan freePlan = planRepo.findByName("Free")
            .orElseThrow(() -> new RuntimeException("Plano Free não encontrado"));

    // Verifica se já não tem uma assinatura Free ativa
    boolean hasActive = subscriptionRepo.existsByTenantIdAndStatus(
            tenantId, SubscriptionStatus.ACTIVE);
    if (hasActive) return;

    Subscription freeSub = new Subscription();
    freeSub.setTenant(tenantRepo.findById(tenantId).orElseThrow());
    freeSub.setPlan(freePlan);
    freeSub.setStatus(SubscriptionStatus.ACTIVE);
    freeSub.setStartDate(LocalDate.now());
    freeSub.setEndDate(null);
    subscriptionRepo.save(freeSub);
  }
}