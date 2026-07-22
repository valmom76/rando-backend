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
@Table(name = "friendly_session_attendance")
@Getter
@Setter
public class FriendlySessionAttendance {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(length = 36)
  @JdbcTypeCode(SqlTypes.CHAR)
  private UUID id;

  @Column(name = "tenant_id", nullable = false, length = 36)
  @JdbcTypeCode(SqlTypes.CHAR)
  private UUID tenantId;

  @Column(name = "session_id", nullable = false, length = 36)
  @JdbcTypeCode(SqlTypes.CHAR)
  private UUID sessionId;

  @Column(name = "player_id", nullable = false, length = 36)
  @JdbcTypeCode(SqlTypes.CHAR)
  private UUID playerId;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;
}
