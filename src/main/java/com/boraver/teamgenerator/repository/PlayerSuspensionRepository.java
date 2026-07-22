package com.boraver.teamgenerator.repository;

import com.boraver.teamgenerator.entity.PlayerSuspension;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PlayerSuspensionRepository extends JpaRepository<PlayerSuspension, UUID> {
  List<PlayerSuspension> findByChampionshipIdOrderByCreatedAtDesc(UUID championshipId);
  List<PlayerSuspension> findByChampionshipIdAndStatus(UUID championshipId, String status);
  List<PlayerSuspension> findByChampionshipIdAndPlayerIdAndStatus(
          UUID championshipId, UUID playerId, String status);
  void deleteByChampionshipId(UUID championshipId);
}
