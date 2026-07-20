package com.boraver.teamgenerator.repository;

import com.boraver.teamgenerator.entity.GeneratedTeams;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GeneratedTeamsRepository extends JpaRepository<GeneratedTeams, UUID> {

  Optional<GeneratedTeams> findTopByTenantIdAndActiveTrueOrderByCreatedAtDesc(UUID tenantId);

  @Modifying
  @Query("UPDATE GeneratedTeams g SET g.active = false WHERE g.tenantId = :tenantId AND g.active = true")
  void deactivatePreviousGenerations(@Param("tenantId") UUID tenantId);
}