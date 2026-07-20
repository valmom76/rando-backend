package com.boraver.teamgenerator.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.UUID;

@Entity
@Table(name = "player_position")
@Getter
@Setter
public class PlayerPosition {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @JdbcTypeCode(SqlTypes.CHAR)
  @Column(length = 36)
  private UUID id;

  @Column(name = "player_id", nullable = false)
  @JdbcTypeCode(SqlTypes.CHAR)
  private UUID playerId;

  @Column(name = "position_id", nullable = false)
  @JdbcTypeCode(SqlTypes.CHAR)
  private UUID positionId;

  @Column(nullable = false)
  private int priority;

  @Column(name = "tenant_id", nullable = false)
  @JdbcTypeCode(SqlTypes.CHAR)
  private UUID tenantId;
}