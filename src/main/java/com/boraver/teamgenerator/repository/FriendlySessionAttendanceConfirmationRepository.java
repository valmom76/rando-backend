package com.boraver.teamgenerator.repository;

import com.boraver.teamgenerator.entity.FriendlySessionAttendanceConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Collection;
import java.util.UUID;

public interface FriendlySessionAttendanceConfirmationRepository
        extends JpaRepository<FriendlySessionAttendanceConfirmation, UUID> {

  Optional<FriendlySessionAttendanceConfirmation> findBySessionIdAndTenantId(
          UUID sessionId, UUID tenantId);

  boolean existsBySessionIdAndTenantId(UUID sessionId, UUID tenantId);

  List<FriendlySessionAttendanceConfirmation> findByTenantIdAndSessionIdIn(
          UUID tenantId, Collection<UUID> sessionIds);

  List<FriendlySessionAttendanceConfirmation>
  findByTenantIdAndSessionDateBetweenOrderBySessionDateAsc(
          UUID tenantId, LocalDate from, LocalDate to);

  void deleteBySessionIdAndTenantId(UUID sessionId, UUID tenantId);
}
