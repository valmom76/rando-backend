package com.boraver.teamgenerator.repository;

import com.boraver.teamgenerator.entity.Championship;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ChampionshipRepository extends JpaRepository<Championship, UUID> {
  List<Championship> findByTenantIdOrderByCreatedAtDesc(UUID tenantId);
  long countByTenantId(UUID tenantId);
  long countByTenantIdAndStatus(UUID tenantId, String status);
}