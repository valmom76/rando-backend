package com.boraver.teamgenerator.repository;

import com.boraver.teamgenerator.entity.DisciplinaryAppeal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DisciplinaryAppealRepository extends JpaRepository<DisciplinaryAppeal, UUID> {
  List<DisciplinaryAppeal> findByChampionshipIdOrderByCreatedAtDesc(UUID championshipId);
  Optional<DisciplinaryAppeal> findFirstBySuspensionIdAndStatusOrderByCreatedAtDesc(
          UUID suspensionId, String status);
  Optional<DisciplinaryAppeal> findFirstBySuspensionIdOrderByCreatedAtDesc(UUID suspensionId);
  void deleteByChampionshipId(UUID championshipId);
}
