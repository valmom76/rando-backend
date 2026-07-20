package com.boraver.teamgenerator.repository;

import com.boraver.teamgenerator.entity.ChampionshipTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ChampionshipTeamRepository extends JpaRepository<ChampionshipTeam, UUID> {
  List<ChampionshipTeam> findByChampionshipId(UUID championshipId);
}