package com.boraver.teamgenerator.repository;

import com.boraver.teamgenerator.entity.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GameSessionRepository extends JpaRepository<GameSession, UUID> {
  Optional<GameSession> findByTenantIdAndActiveTrue(UUID tenantId);
}
