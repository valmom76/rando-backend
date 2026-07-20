package com.boraver.teamgenerator.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "championship_teams")
@Data
public class ChampionshipTeam {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @JdbcTypeCode(SqlTypes.CHAR)
  @Column(length = 36)
  private UUID id;

  @Column(name = "championship_id", nullable = false)
  @JdbcTypeCode(SqlTypes.CHAR)
  private UUID championshipId;

  @Column(name = "name")
  private String name; // Se null, o frontend exibirá "Time {teamIndex}"

  @Column(name = "team_index", nullable = false)
  private int teamIndex; // índice do time original (Time 1, Time 2, etc.)

  @Column(name = "group_index")
  private Integer groupIndex; // grupo em que foi alocado (0-based ou 1-based)

  @Column(name = "seed", nullable = false)
  private int seed; // posição no ranking (1 = melhor rating)

  @Column(name = "initial_score")
  private BigDecimal initialScore; // rating médio do time (opcional)
}