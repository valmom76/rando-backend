package com.boraver.teamgenerator.repository;

import com.boraver.teamgenerator.entity.MatchGoalEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MatchGoalEventRepository extends JpaRepository<MatchGoalEvent, UUID> {
  List<MatchGoalEvent> findByMatchIdOrderByCreatedAtAsc(UUID matchId);
  List<MatchGoalEvent> findByChampionshipIdOrderByCreatedAtAsc(UUID championshipId);
  void deleteByMatchId(UUID matchId);
  void deleteByChampionshipId(UUID championshipId);
}
