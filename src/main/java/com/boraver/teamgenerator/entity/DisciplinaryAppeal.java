package com.boraver.teamgenerator.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "disciplinary_appeals")
@Data
public class DisciplinaryAppeal {
  @Id @GeneratedValue(strategy = GenerationType.UUID)
  @JdbcTypeCode(SqlTypes.CHAR) @Column(length = 36) private UUID id;
  @JdbcTypeCode(SqlTypes.CHAR) @Column(name = "championship_id", nullable = false, length = 36) private UUID championshipId;
  @JdbcTypeCode(SqlTypes.CHAR) @Column(name = "suspension_id", nullable = false, length = 36) private UUID suspensionId;
  @Column(nullable = false, length = 1000) private String reason;
  @Column(nullable = false, length = 20) private String status = "PENDING";
  @Column(name = "decision_notes", length = 1000) private String decisionNotes;
  @Column(name = "decided_by", length = 180) private String decidedBy;
  @Column(name = "created_at", nullable = false) private LocalDateTime createdAt = LocalDateTime.now();
  @Column(name = "decided_at") private LocalDateTime decidedAt;
}
