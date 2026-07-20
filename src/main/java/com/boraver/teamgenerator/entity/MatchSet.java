package com.boraver.teamgenerator.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "match_set")
@Getter
@Setter
public class MatchSet {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @JdbcTypeCode(SqlTypes.CHAR) @Column(length = 36)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "match_id", nullable = false)
  private ChampionshipMatch match;

  @Column(name = "set_number", nullable = false)
  private int setNumber;

  @Column(name = "home_score", nullable = false)
  private int homeScore;

  @Column(name = "away_score", nullable = false)
  private int awayScore;
}
