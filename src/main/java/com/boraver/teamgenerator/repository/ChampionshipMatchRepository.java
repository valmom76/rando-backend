package com.boraver.teamgenerator.repository;

import com.boraver.teamgenerator.entity.ChampionshipMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ChampionshipMatchRepository extends JpaRepository<ChampionshipMatch, UUID> {
  List<ChampionshipMatch> findByChampionshipIdOrderByRoundAsc(UUID championshipId);
  List<ChampionshipMatch> findByChampionshipIdAndGroupIndexOrderByRoundAsc(UUID championshipId, int groupIndex);
  List<ChampionshipMatch> findByChampionshipIdAndStage(UUID championshipId, String stage);
  List<ChampionshipMatch> findByChampionshipIdAndStageNot(UUID championshipId, String stage);

  @Query("SELECT COUNT(m) FROM ChampionshipMatch m JOIN Championship c ON c.id = m.championshipId WHERE c.tenantId = :tenantId")
  long countByChampionshipTenantId(@Param("tenantId") UUID tenantId);
}