package com.boraver.teamgenerator.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "match_results")
@Data
public class MatchResult {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @JdbcTypeCode(SqlTypes.CHAR)
  @Column(length = 36)
  private UUID id;

  @Column(name = "tenant_id", nullable = false)
  @JdbcTypeCode(SqlTypes.CHAR)
  private UUID tenantId;

  @Column(name = "created_at", nullable = false)
  @CreationTimestamp
  private LocalDateTime createdAt;

  @ManyToMany
  @JoinTable(
      name = "match_result_players",
      joinColumns = @JoinColumn(name = "match_result_id"),
      inverseJoinColumns = @JoinColumn(name = "player_id")
  )
  private Set<Player> winningTeam = new HashSet<>();

  @ManyToMany
  @JoinTable(
      name = "match_losing_players",
      joinColumns = @JoinColumn(name = "match_result_id"),
      inverseJoinColumns = @JoinColumn(name = "player_id")
  )
  private Set<Player> losingTeam = new HashSet<>();

  @ManyToOne
  @JoinColumn(name = "team_generation_id")
  private TeamGenerationSession teamGenerationSession;

  private int teamScore;
  private int opponentScore;
}