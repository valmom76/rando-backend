package com.boraver.teamgenerator.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "match_goal_events")
@Data
public class MatchGoalEvent {
  @Id @GeneratedValue(strategy = GenerationType.UUID)
  @JdbcTypeCode(SqlTypes.CHAR) @Column(length = 36) private UUID id;
  @JdbcTypeCode(SqlTypes.CHAR) @Column(name = "championship_id", nullable = false, length = 36) private UUID championshipId;
  @JdbcTypeCode(SqlTypes.CHAR) @Column(name = "match_id", nullable = false, length = 36) private UUID matchId;
  @Column(name = "scoring_team_index", nullable = false) private int scoringTeamIndex;
  @Column(name = "scorer_team_index", nullable = false) private int scorerTeamIndex;
  @JdbcTypeCode(SqlTypes.CHAR) @Column(name = "player_id", nullable = false, length = 36) private UUID playerId;
  @Column(name = "own_goal", nullable = false) private boolean ownGoal;
  @Column(name = "match_minute") private Integer matchMinute;
  @Column(name = "created_at", nullable = false) private LocalDateTime createdAt = LocalDateTime.now();
}
