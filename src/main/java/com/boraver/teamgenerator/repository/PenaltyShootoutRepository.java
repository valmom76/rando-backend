package com.boraver.teamgenerator.repository;

import com.boraver.teamgenerator.entity.PenaltyShootout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PenaltyShootoutRepository extends JpaRepository<PenaltyShootout, UUID> {
  Optional<PenaltyShootout> findByMatchId(UUID matchId);
}
