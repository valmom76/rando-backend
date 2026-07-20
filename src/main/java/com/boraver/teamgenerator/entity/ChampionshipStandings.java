package com.boraver.teamgenerator.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "championship_standings")
@Data
public class ChampionshipStandings {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @JdbcTypeCode(SqlTypes.CHAR)
  @Column(length = 36)
  private UUID id;

  @Column(name = "championship_id", nullable = false)
  @JdbcTypeCode(SqlTypes.CHAR)
  private UUID championshipId;

  @Column(name = "team_index", nullable = false)
  private int teamIndex;

  @Column(name = "group_index")
  private Integer groupIndex;

  private int points;
  private int played;
  private int wins;
  private int draws;
  private int losses;
  private int goalsFor;
  private int goalsAgainst;
  private int goalsDifference;

  @Column(name = "last_update")
  private LocalDateTime lastUpdate;

  @Column(name = "sets_won", nullable = false)
  private int setsWon = 0;

  @Column(name = "sets_lost", nullable = false)
  private int setsLost = 0;

  @Column(name = "sets_difference", nullable = false)
  private int setsDifference = 0;
}