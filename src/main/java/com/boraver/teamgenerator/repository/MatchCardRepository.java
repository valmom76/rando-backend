package com.boraver.teamgenerator.repository;

import com.boraver.teamgenerator.entity.MatchCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MatchCardRepository extends JpaRepository<MatchCard, UUID> {
  List<MatchCard> findByMatchIdOrderByCreatedAtAsc(UUID matchId);
  List<MatchCard> findByChampionshipIdAndPlayerIdAndCardType(
          UUID championshipId, UUID playerId, String cardType);
  void deleteByChampionshipId(UUID championshipId);
}
