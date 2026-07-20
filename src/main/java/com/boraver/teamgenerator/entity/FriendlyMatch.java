package com.boraver.teamgenerator.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "friendly_matches")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FriendlyMatch {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @JdbcTypeCode(SqlTypes.CHAR)
  @Column(columnDefinition = "CHAR(36)")
  private UUID id;

  @Column(name = "session_id", nullable = false, columnDefinition = "CHAR(36)")
  @JdbcTypeCode(SqlTypes.CHAR)
  private UUID sessionId;

  @Column(name = "court_name", nullable = false)
  private String courtName;

  @Column(name = "home_team_index", nullable = false)
  private Integer homeTeamIndex;

  @Column(name = "away_team_index", nullable = false)
  private Integer awayTeamIndex;

  @Column(name = "home_score")
  private Integer homeScore = 0;

  @Column(name = "away_score")
  private Integer awayScore = 0;

  @Column(name = "winner_team_index")
  private Integer winnerTeamIndex;

  @Column(name = "played")
  private boolean played;

  @Column(name = "status")
  private String status = "pending";

  @Column(name = "walkover")
  private boolean walkover;

  @Column(name = "created_at")
  private LocalDateTime createdAt = LocalDateTime.now();
}