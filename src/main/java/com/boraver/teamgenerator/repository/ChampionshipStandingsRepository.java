package com.boraver.teamgenerator.repository;

import com.boraver.teamgenerator.entity.ChampionshipStandings;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChampionshipStandingsRepository extends JpaRepository<ChampionshipStandings, UUID> {
  List<ChampionshipStandings> findByChampionshipIdOrderByGroupIndexAscPointsDescGoalsDifferenceDescGoalsForAsc(UUID championshipId);
  Optional<ChampionshipStandings> findByChampionshipIdAndTeamIndex(UUID championshipId, int teamIndex);
  List<ChampionshipStandings> findByChampionshipIdOrderByGroupIndexAscPointsDescSetsDifferenceDescGoalsDifferenceDescGoalsForAsc(UUID championshipId);
}