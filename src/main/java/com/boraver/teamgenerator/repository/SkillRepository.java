package com.boraver.teamgenerator.repository;

import com.boraver.teamgenerator.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

public interface SkillRepository extends JpaRepository<Skill, UUID> {
  List<Skill> findAllByTenantIdAndActiveTrue(UUID tenantId);
  Optional<Skill> findByIdAndTenantId(UUID id, UUID tenantId);
  Optional<Skill> findByTenantIdAndNameIgnoreCase(UUID tenantId, String name);
  long countByTenantIdAndActiveTrue(UUID tenantId);

  @Query("UPDATE Skill p SET p.active = false WHERE p.id = :id")
  @Modifying
  @Transactional
  void softDelete(@Param("id") UUID id);
}
