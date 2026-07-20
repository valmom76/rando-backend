package com.boraver.teamgenerator.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name="generated_team_player")
@Getter
@Setter
public class GeneratedTeamPlayer {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @JdbcTypeCode(SqlTypes.CHAR)
  @Column(length = 36)
  private UUID id;

  @Column(name="team_id", nullable=false)
  @JdbcTypeCode(SqlTypes.CHAR)
  private UUID teamId;

  @Column(name="player_id", nullable=false)
  @JdbcTypeCode(SqlTypes.CHAR)
  private UUID playerId;

  @Column(name="sex_at_generation", nullable=false, length=1)
  private String sexAtGeneration;

  @Column(name="score_at_generation", nullable=false, precision=10, scale=4)
  private BigDecimal scoreAtGeneration;

  @Column(name="snapshot_json", nullable=false, columnDefinition="json")
  private String snapshotJson;
}