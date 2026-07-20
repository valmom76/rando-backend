package com.boraver.teamgenerator.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "championships")
@Data
public class Championship {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @JdbcTypeCode(SqlTypes.CHAR)
  @Column(length = 36)
  private UUID id;

  @Column(name = "tenant_id", nullable = false)
  @JdbcTypeCode(SqlTypes.CHAR)
  private UUID tenantId;

  @Column(nullable = false)
  private String name;

  @Column(name = "created_at")
  private LocalDateTime createdAt = LocalDateTime.now();

  @Column(name = "started_at")
  private LocalDateTime startedAt;

  @Column(name = "ended_at")
  private LocalDateTime endedAt;

  @Column(name = "team_count", nullable = false)
  private int teamCount; // número de times participantes

  @Column(name = "groups_count", nullable = false)
  private int groupsCount; // número de grupos

  @Column(name = "teams_per_group",  nullable = false)
  private int teamsPerGroup; // times por grupo

  @Column(name = "qualified_per_group", nullable = false)
  private int qualifiedPerGroup; // quantos se classificam por grupo

  @Column(name = "matches_type", nullable = false)
  private String matchesType; // "HOME_AND_AWAY" ou "SINGLE"

  @Column(name = "points_per_win")
  private int pointsPerWin = 3;

  @Column(name = "status", nullable = false)
  private String status; // "CREATED", "IN_PROGRESS", "FINISHED"

  @ManyToOne
  @JoinColumn(name = "generation_session_id")
  private TeamGenerationSession generationSession;

  @Column(nullable = false)
  private String format;

  @Enumerated(EnumType.STRING)
  @Column(name = "sport_type", nullable = false, length = 20)
  private SportType sportType = SportType.VOLLEYBALL;

  @Column(name = "default_sets_to_win", nullable = false)
  private int defaultSetsToWin = 2;

  @Column(name = "default_points_per_set", nullable = false)
  private int defaultPointsPerSet = 25;

  @Column(name = "default_tie_break_points", nullable = false)
  private int defaultTieBreakPoints = 15;
}
