package com.boraver.teamgenerator.repository;

import com.boraver.teamgenerator.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PositionRepository extends JpaRepository<Position, UUID> {
  List<Position> findAllByTenantIdAndActiveTrue(UUID tenantId);
  Optional<Position> findByTenantIdAndNameIgnoreCase(UUID tenantId, String name);
  long countByTenantIdAndActiveTrue(UUID tenantId);
  Optional<Position> findByIdAndTenantId(UUID id, UUID tenantId);

  @Query("UPDATE Position p SET p.active = false WHERE p.id = :id")
  @Modifying
  @Transactional
  void softDelete(@Param("id") UUID id);
}