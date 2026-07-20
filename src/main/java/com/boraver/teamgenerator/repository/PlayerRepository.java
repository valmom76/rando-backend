package com.boraver.teamgenerator.repository;

import com.boraver.teamgenerator.dto.player.PlayerResponse;
import com.boraver.teamgenerator.entity.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

public interface PlayerRepository extends JpaRepository<Player, UUID> {
  List<Player> findAllByTenantIdAndActiveTrue(UUID tenantId, Sort sort);
  List<Player> findAllByTenantIdAndIdIn(UUID tenantId, List<UUID> ids);
  Optional<Player> findByIdAndTenantId(UUID id, UUID tenantId);
  List<Player> findAllByIdInAndTenantId(List<UUID> ids, UUID tenantId);
  Optional<Player> findByTenantIdAndNameIgnoreCase(UUID tenantId, String name);
  long countByTenantIdAndActiveTrue(UUID tenantId);

  @Query(value = """
    SELECT
        p.name,
        MAX(tgs.created_at) AS last_date
    FROM player p
    LEFT JOIN generated_team_player gtp ON gtp.player_id = p.id
    LEFT JOIN generated_team gt ON gt.id = gtp.team_id
    LEFT JOIN team_generation_session tgs ON tgs.id = gt.session_id
    WHERE p.tenant_id = :tenantId
      AND p.active = true
    GROUP BY p.id, p.name
    HAVING MAX(tgs.created_at) < NOW() - INTERVAL 30 DAY
        OR MAX(tgs.created_at) IS NULL
    ORDER BY MAX(tgs.created_at) ASC
  """, nativeQuery = true)
  List<Object[]> findInactivePlayersRaw(@Param("tenantId") String tenantId);

  @Query("""
    SELECT DISTINCT p FROM Player p
    LEFT JOIN FETCH p.positions
    WHERE p.tenantId = :tenantId AND p.active = true
    ORDER BY p.name
  """)
  List<Player> findAllActiveWithPositions(@Param("tenantId") UUID tenantId);

  @Query("UPDATE Player p SET p.active = false WHERE p.id = :id")
  @Modifying
  @Transactional
  void softDelete(@Param("id") UUID id);
}
