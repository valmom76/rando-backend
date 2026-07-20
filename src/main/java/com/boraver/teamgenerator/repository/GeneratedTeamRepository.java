package com.boraver.teamgenerator.repository;

import com.boraver.teamgenerator.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface GeneratedTeamRepository extends JpaRepository<GeneratedTeam, UUID> {
  List<GeneratedTeam> findAllBySessionIdOrderByTeamIndexAsc(UUID sessionId);
  List<GeneratedTeam> findAllBySessionIdAndTeamIndexIn(UUID sessionId, List<Integer> teamIndices);
  int countBySessionId(UUID sessionId);
  Optional<GeneratedTeam> findBySessionIdAndTeamIndex(UUID sessionId, int teamIndex);
  List<GeneratedTeam> findBySessionIdOrderByTeamIndex(UUID sessionId);

  @Query("SELECT COUNT(gt) FROM GeneratedTeam gt JOIN TeamGenerationSession s ON s.id = gt.sessionId WHERE s.tenantId = :tenantId")
  long countByTenantId(@Param("tenantId") UUID tenantId);
}