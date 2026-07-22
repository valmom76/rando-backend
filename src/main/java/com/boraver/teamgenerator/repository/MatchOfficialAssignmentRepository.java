package com.boraver.teamgenerator.repository;

import com.boraver.teamgenerator.entity.MatchOfficialAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MatchOfficialAssignmentRepository extends JpaRepository<MatchOfficialAssignment, UUID> {
  List<MatchOfficialAssignment> findByMatchId(UUID matchId);
  void deleteByMatchId(UUID matchId);
  void deleteByChampionshipId(UUID championshipId);
}
