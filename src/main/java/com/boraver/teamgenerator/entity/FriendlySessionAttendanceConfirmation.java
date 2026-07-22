package com.boraver.teamgenerator.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "friendly_session_attendance_confirmations")
@Getter
@Setter
public class FriendlySessionAttendanceConfirmation {

  @Id
  @Column(name = "session_id", length = 36)
  @JdbcTypeCode(SqlTypes.CHAR)
  private UUID sessionId;

  @Column(name = "tenant_id", nullable = false, length = 36)
  @JdbcTypeCode(SqlTypes.CHAR)
  private UUID tenantId;

  @Column(name = "session_date", nullable = false)
  private LocalDate sessionDate;

  @Column(name = "confirmed_by", nullable = false, length = 36)
  @JdbcTypeCode(SqlTypes.CHAR)
  private UUID confirmedBy;

  @Column(name = "confirmed_at", nullable = false)
  private LocalDateTime confirmedAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;
}
