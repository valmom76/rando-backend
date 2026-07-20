package com.boraver.teamgenerator.repository;

import com.boraver.teamgenerator.entity.PlayerPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

public interface PlayerPositionRepository extends JpaRepository<PlayerPosition, UUID> {
  List<PlayerPosition> findAllByPlayerIdOrderByPriorityAsc(UUID playerId);

  @Modifying
  @Query("DELETE FROM PlayerPosition pp WHERE pp.playerId = :playerId")
  void deleteAllByPlayerId(@Param("playerId") UUID playerId);

  List<PlayerPosition> findAllByPlayerIdIn(List<UUID> selectedPlayerIds);
}