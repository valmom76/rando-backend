package com.boraver.teamgenerator.repository;

import com.boraver.teamgenerator.entity.FootballReferee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FootballRefereeRepository extends JpaRepository<FootballReferee, UUID> {
  List<FootballReferee> findByTenantIdAndActiveTrueOrderByNameAsc(UUID tenantId);
  Optional<FootballReferee> findByIdAndTenantIdAndActiveTrue(UUID id, UUID tenantId);
}
