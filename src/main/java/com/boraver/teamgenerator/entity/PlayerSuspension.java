package com.boraver.teamgenerator.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "player_suspensions")
@Data
public class PlayerSuspension {
  @Id @GeneratedValue(strategy = GenerationType.UUID)
  @JdbcTypeCode(SqlTypes.CHAR) @Column(length = 36) private UUID id;
  @JdbcTypeCode(SqlTypes.CHAR) @Column(name = "championship_id", nullable = false, length = 36) private UUID championshipId;
  @JdbcTypeCode(SqlTypes.CHAR) @Column(name = "player_id", nullable = false, length = 36) private UUID playerId;
  @Column(name = "team_index", nullable = false) private int teamIndex;
  @JdbcTypeCode(SqlTypes.CHAR) @Column(name = "source_match_id", nullable = false, length = 36) private UUID sourceMatchId;
  @JdbcTypeCode(SqlTypes.CHAR) @Column(name = "source_card_id", nullable = false, length = 36) private UUID sourceCardId;
  @Column(name = "suspension_reason", nullable = false, length = 40) private String suspensionReason;
  @Column(name = "total_matches", nullable = false) private int totalMatches;
  @Column(name = "remaining_matches", nullable = false) private int remainingMatches;
  @Column(nullable = false, length = 30) private String status = "ACTIVE";
  @Column(name = "created_at", nullable = false) private LocalDateTime createdAt = LocalDateTime.now();
  @Column(name = "updated_at", nullable = false) private LocalDateTime updatedAt = LocalDateTime.now();
}
