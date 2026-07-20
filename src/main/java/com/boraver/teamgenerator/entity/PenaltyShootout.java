package com.boraver.teamgenerator.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "match_penalty_shootouts")
@Getter
@Setter
public class PenaltyShootout {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @JdbcTypeCode(SqlTypes.CHAR)
  @Column(length = 36)
  private UUID id;

  @Column(name = "championship_id", nullable = false)
  @JdbcTypeCode(SqlTypes.CHAR)
  private UUID championshipId;

  @Column(name = "match_id", nullable = false, unique = true)
  @JdbcTypeCode(SqlTypes.CHAR)
  private UUID matchId;

  @Column(name = "home_team_index", nullable = false)
  private int homeTeamIndex;

  @Column(name = "away_team_index", nullable = false)
  private int awayTeamIndex;

  @Column(name = "home_score", nullable = false)
  private int homeScore;

  @Column(name = "away_score", nullable = false)
  private int awayScore;

  @Column(name = "winner_team_index", nullable = false)
  private int winnerTeamIndex;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();
}
