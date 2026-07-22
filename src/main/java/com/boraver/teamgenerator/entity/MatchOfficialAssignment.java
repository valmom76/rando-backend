package com.boraver.teamgenerator.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "match_official_assignments")
@Data
public class MatchOfficialAssignment {
  @Id @GeneratedValue(strategy = GenerationType.UUID)
  @JdbcTypeCode(SqlTypes.CHAR) @Column(length = 36) private UUID id;
  @JdbcTypeCode(SqlTypes.CHAR) @Column(name = "championship_id", nullable = false, length = 36) private UUID championshipId;
  @JdbcTypeCode(SqlTypes.CHAR) @Column(name = "match_id", nullable = false, length = 36) private UUID matchId;
  @JdbcTypeCode(SqlTypes.CHAR) @Column(name = "referee_id", nullable = false, length = 36) private UUID refereeId;
  @Column(name = "official_role", nullable = false, length = 20) private String officialRole;
  @Column(name = "created_at", nullable = false) private LocalDateTime createdAt = LocalDateTime.now();
}
