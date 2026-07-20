package com.boraver.teamgenerator.repository;

import com.boraver.teamgenerator.entity.MatchResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface MatchResultRepository extends JpaRepository<MatchResult, UUID> {
  List<MatchResult> findByTenantIdOrderByCreatedAtDesc(UUID tenantId);

  @Query("SELECT mr FROM MatchResult mr JOIN mr.winningTeam p WHERE p.id = :playerId AND mr.tenantId = :tenantId")
  List<MatchResult> findByPlayerIdAndTenantId(@Param("playerId") UUID playerId, @Param("tenantId") UUID tenantId);

  @Query(value = """
    WITH player_arrays AS (
        SELECT
            mr.id,
            GROUP_CONCAT(mrp.player_id ORDER BY mrp.player_id) AS player_ids
        FROM match_results mr
        JOIN match_result_players mrp ON mr.id = mrp.match_result_id
        WHERE mr.tenant_id = :tenantId
          AND DATE(mr.created_at) = :date
        GROUP BY mr.id
    )
    SELECT player_ids, COUNT(*) AS wins
    FROM player_arrays
    GROUP BY player_ids
    ORDER BY wins DESC
    """, nativeQuery = true)
  List<Object[]> getTeamStats(@Param("tenantId") UUID tenantId, @Param("date") LocalDate date);

  @Query(value = """
    SELECT
        player_ids,
        SUM(win_flag) AS wins,
        SUM(point_diff) AS total_point_diff
    FROM (
        SELECT
            mr.id,
            GROUP_CONCAT(mrp.player_id ORDER BY mrp.player_id) AS player_ids,
            (mr.team_score - mr.opponent_score) AS point_diff,
            CASE WHEN mr.team_score > mr.opponent_score THEN 1 ELSE 0 END AS win_flag
        FROM match_results mr
        JOIN match_result_players mrp ON mr.id = mrp.match_result_id
        JOIN team_generation_session tgs ON tgs.id = mr.team_generation_id
        JOIN game_sessions gs ON gs.id = tgs.session_id
        WHERE mr.tenant_id = :tenantId
          AND gs.active = 1
        GROUP BY mr.id, mr.team_score, mr.opponent_score
    ) AS player_arrays
    GROUP BY player_ids
    ORDER BY wins DESC, total_point_diff DESC
    """, nativeQuery = true)
  List<Object[]> getTeamStatsFromActiveGameSession(@Param("tenantId") String tenantId);
}