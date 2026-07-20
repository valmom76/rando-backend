package com.boraver.teamgenerator.repository;

import com.boraver.teamgenerator.entity.*;
import org.springframework.data.jpa.repository.*;
import java.util.*;


public interface TeamSessionRepository extends JpaRepository<TeamGenerationSession, UUID> {
  List<TeamGenerationSession> findTop50ByTenantIdOrderByCreatedAtDesc(UUID tenantId);
  Optional<TeamGenerationSession> findByIdAndTenantId(UUID id, UUID tenantId);
  Optional<TeamGenerationSession> findTopByTenantIdOrderByCreatedAtDesc(UUID tenantId);
}
