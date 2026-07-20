package com.boraver.teamgenerator.service;

import com.boraver.teamgenerator.common.TenantContext;
import com.boraver.teamgenerator.dto.subscription.CheckoutResponseDTO;
import com.boraver.teamgenerator.dto.subscription.SubscribeRequestDTO;
import com.boraver.teamgenerator.dto.subscription.SubscriptionStatusDTO;
import com.boraver.teamgenerator.entity.*;
import com.boraver.teamgenerator.entity.Subscription.SubscriptionStatus;
import com.boraver.teamgenerator.repository.AppUserRepository;
import com.boraver.teamgenerator.repository.PlanRepository;
import com.boraver.teamgenerator.repository.SubscriptionRepository;
import com.boraver.teamgenerator.repository.TenantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class SubscriptionService {

  private final SubscriptionRepository subscriptionRepo;
  private final PlanRepository planRepo;
  private final TenantRepository tenantRepo;
  private final AppUserRepository appUserRepository;
  private final AsaasService asaasService;

  public SubscriptionService(SubscriptionRepository subscriptionRepo,
                             PlanRepository planRepo,
                             TenantRepository tenantRepo,
                             AppUserRepository appUserRepository,
                             AsaasService asaasService) {
    this.subscriptionRepo = subscriptionRepo;
    this.planRepo = planRepo;
    this.tenantRepo = tenantRepo;
    this.appUserRepository = appUserRepository;
    this.asaasService = asaasService;
  }

  // ─── Upgrade de plano ───

  @Transactional
  public CheckoutResponseDTO subscribe(SubscribeRequestDTO request) {
    UUID tenantId = getTenantId();
    Tenant tenant = tenantRepo.findById(tenantId)
            .orElseThrow(() -> new RuntimeException("Tenant não encontrado"));
    Plan newPlan = planRepo.findById(request.planId())
            .orElseThrow(() -> new RuntimeException("Plano não encontrado"));
    AppUser adminUser = appUserRepository.findFirstByTenantIdAndRole(tenantId, "ADMIN")
            .orElseThrow(() -> new RuntimeException("Admin não encontrado"));

    // Busca assinatura ativa atual
    Subscription currentSub = subscriptionRepo
            .findByTenantIdAndStatus(tenantId, SubscriptionStatus.ACTIVE)
            .orElse(null);

    String customerId;

    if (currentSub != null) {
      // Verifica se é o mesmo plano
      if (currentSub.getPlan().getId().equals(newPlan.getId())) {
        throw new RuntimeException("Você já está neste plano!");
      }

      // Só permite upgrade (plano mais caro)
      if (newPlan.getPrice().compareTo(currentSub.getPlan().getPrice()) <= 0) {
        throw new RuntimeException(
                "Downgrade não é permitido manualmente. " +
                        "Seu plano será alterado automaticamente para Free ao final do ciclo " +
                        "caso não haja renovação."
        );
      }

      // Upgrade: cancela assinatura atual no Asaas
      if (currentSub.getAsaasSubscriptionId() != null &&
              !currentSub.getAsaasSubscriptionId().isBlank()) {
        try {
          asaasService.cancelSubscription(currentSub.getAsaasSubscriptionId());
        } catch (Exception e) {
          System.err.println("Erro ao cancelar assinatura anterior no Asaas: " + e.getMessage());
        }
      }
      currentSub.setStatus(SubscriptionStatus.CANCELLED);
      subscriptionRepo.save(currentSub);

      // Reutiliza customerId
      customerId = currentSub.getAsaasCustomerId();
    } else {
      // Primeira assinatura: cria customer novo
      customerId = asaasService.getOrCreateCustomer(adminUser);
    }

    // Cria nova assinatura no Asaas (vencimento hoje + 30 dias)
    Map<String, Object> asaasSub = asaasService.createSubscription(customerId, newPlan, tenantId);

    // Salva local
    Subscription sub = new Subscription();
    sub.setTenant(tenant);
    sub.setPlan(newPlan);
    sub.setStatus(SubscriptionStatus.PENDING);
    sub.setStartDate(LocalDate.now());
    sub.setEndDate(null);
    sub.setAsaasSubscriptionId((String) asaasSub.get("id"));
    sub.setAsaasCustomerId(customerId);
    subscriptionRepo.save(sub);

    // Obtém detalhes da primeira cobrança
    String paymentId = extractPaymentId(asaasSub);
    Map<String, Object> paymentDetails = asaasService.getPaymentDetails(paymentId);

    String invoiceUrl = (String) paymentDetails.get("invoiceUrl");
    String bankSlipUrl = (String) paymentDetails.get("bankSlipUrl");
    String pixUrl = (String) paymentDetails.get("pixUrl");

    return new CheckoutResponseDTO(
            sub.getId(),
            sub.getStatus().name(),
            bankSlipUrl,
            pixUrl,
            invoiceUrl
    );
  }

  // ─── Status da assinatura ───

  public SubscriptionStatusDTO getSubscriptionStatus() {
    UUID tenantId = getTenantId();
    Subscription sub = subscriptionRepo
            .findFirstByTenantIdAndStatusNotOrderByStartDateDesc(
                    tenantId, SubscriptionStatus.CANCELLED)
            .orElse(null);

    // Se não tem assinatura ou está PENDING, retorna Free
    if (sub == null || sub.getStatus() == SubscriptionStatus.PENDING) {
      Plan freePlan = planRepo.findByName("Free").orElse(null);
      List<String> features = freePlan != null ? freePlan.getFeatureList() : List.of();
      return new SubscriptionStatusDTO(false, "Free", "Nenhuma assinatura ativa", features);
    }

    boolean active = sub.getStatus() == SubscriptionStatus.ACTIVE;
    String message = active
            ? "Sua assinatura está ativa"
            : "Assinatura suspensa por falta de pagamento";

    return new SubscriptionStatusDTO(
            active,
            sub.getPlan().getName(),
            message,
            sub.getPlan().getFeatureList()
    );
  }

  // ─── Métodos auxiliares ───

  private UUID getTenantId() {
    String tenantIdStr = TenantContext.getTenantId();
    if (tenantIdStr == null) {
      throw new IllegalStateException("Tenant não encontrado no contexto");
    }
    return UUID.fromString(tenantIdStr);
  }

  private String extractPaymentId(Map<String, Object> asaasSub) {
    // Tenta pegar firstPayment
    Object firstPaymentObj = asaasSub.get("firstPayment");
    if (firstPaymentObj instanceof Map) {
      Map<String, Object> firstPayment = (Map<String, Object>) firstPaymentObj;
      return (String) firstPayment.get("id");
    }
    // Tenta firstPaymentId
    if (asaasSub.get("firstPaymentId") != null) {
      return asaasSub.get("firstPaymentId").toString();
    }
    // Fallback: busca pagamentos da assinatura
    Map<String, Object> paymentsData = asaasService.getPaymentsBySubscription(
            (String) asaasSub.get("id"));
    List<Map<String, Object>> paymentList = (List<Map<String, Object>>) paymentsData.get("data");
    if (paymentList != null && !paymentList.isEmpty()) {
      return (String) paymentList.get(0).get("id");
    }
    throw new RuntimeException("Nenhum pagamento encontrado para a assinatura");
  }
}