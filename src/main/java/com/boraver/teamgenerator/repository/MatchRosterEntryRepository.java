package com.boraver.teamgenerator.repository;

import com.boraver.teamgenerator.entity.MatchRosterEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MatchRosterEntryRepository extends JpaRepository<MatchRosterEntry, UUID> {
  List<MatchRosterEntry> findByMatchId(UUID matchId);
  Optional<MatchRosterEntry> findByMatchIdAndPlayerId(UUID matchId, UUID playerId);
  void deleteByMatchId(UUID matchId);
  void deleteByChampionshipId(UUID championshipId);
}
