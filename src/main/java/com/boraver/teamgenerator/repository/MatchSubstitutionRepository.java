package com.boraver.teamgenerator.repository;

import com.boraver.teamgenerator.entity.MatchSubstitution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MatchSubstitutionRepository extends JpaRepository<MatchSubstitution, UUID> {
  List<MatchSubstitution> findByMatchIdOrderByCreatedAtAsc(UUID matchId);
  void deleteByChampionshipId(UUID championshipId);
}
