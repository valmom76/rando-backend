package com.boraver.teamgenerator.repository;

import com.boraver.teamgenerator.entity.FriendlySessionAttendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface FriendlySessionAttendanceRepository
        extends JpaRepository<FriendlySessionAttendance, UUID> {

  List<FriendlySessionAttendance> findBySessionIdAndTenantId(
          UUID sessionId, UUID tenantId);

  List<FriendlySessionAttendance> findByTenantIdAndSessionIdIn(
          UUID tenantId, Collection<UUID> sessionIds);

  void deleteBySessionIdAndTenantId(UUID sessionId, UUID tenantId);
}
