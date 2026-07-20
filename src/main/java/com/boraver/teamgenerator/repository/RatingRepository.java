package com.boraver.teamgenerator.repository;

import com.boraver.teamgenerator.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.*;

public interface RatingRepository extends JpaRepository<PlayerSkillRating, UUID> {

  @Query("""
    select r from PlayerSkillRating r
    where r.tenantId = :tenantId
      and r.playerId = :playerId
      and r.skillId = :skillId
      and r.validTo is null
  """)
  Optional<PlayerSkillRating> findCurrent(
      @Param("tenantId") UUID tenantId,
      @Param("playerId") UUID playerId,
      @Param("skillId") UUID skillId
  );

  @Query("""
    select r from PlayerSkillRating r
    where r.tenantId = :tenantId
      and r.playerId in :playerIds
      and r.skillId in :skillIds
      and r.validTo is null
  """)
  List<PlayerSkillRating> findCurrentRatings(UUID tenantId, List<UUID> playerIds, List<UUID> skillIds);

  @Modifying
  @Query("""
    update PlayerSkillRating r
      set r.validTo = CURRENT_TIMESTAMP
    where r.tenantId = :tenantId
      and r.playerId = :playerId
      and r.skillId = :skillId
      and r.validTo is null
  """)
  int closeCurrent(UUID tenantId, UUID playerId, UUID skillId);

  @Query("""
    select r from PlayerSkillRating r
    where r.tenantId = :tenantId
      and r.playerId = :playerId
    order by r.validFrom desc
  """)
  List<PlayerSkillRating> findAllByPlayer(UUID tenantId, UUID playerId);

  @Query("SELECT MAX(r.validFrom) FROM PlayerSkillRating r WHERE r.playerId = :playerId AND r.validTo IS NULL")
  LocalDateTime findLastRatingDateByPlayerId(@Param("playerId") UUID playerId);

  @Query("""
    select r from PlayerSkillRating r
    where r.tenantId = :tenantId
      and r.playerId = :playerId
    order by r.validFrom asc
  """)
  List<PlayerSkillRating> findAllByPlayerOrderByValidFromAsc(
          @Param("tenantId") UUID tenantId,
          @Param("playerId") UUID playerId
  );

  @Query("""
    SELECT psr FROM PlayerSkillRating psr
    WHERE psr.tenantId = :tenantId
      AND psr.playerId IN :playerIds
      AND psr.validTo IS NULL
  """)
  List<PlayerSkillRating> findCurrentRatingsForPlayers(
    @Param("tenantId") UUID tenantId,
    @Param("playerIds") List<UUID> playerIds
  );

  @Query("""
    SELECT psr.playerId, MAX(psr.validFrom)
    FROM PlayerSkillRating psr
    WHERE psr.tenantId = :tenantId
      AND psr.playerId IN :playerIds
      AND psr.validTo IS NULL
    GROUP BY psr.playerId
  """)
  List<Object[]> findLastRatingDatesForPlayers(
          @Param("tenantId") UUID tenantId,
          @Param("playerIds") List<UUID> playerIds
  );
}


