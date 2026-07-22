package com.boraver.teamgenerator.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "match_cards")
@Data
public class MatchCard {
  @Id @GeneratedValue(strategy = GenerationType.UUID)
  @JdbcTypeCode(SqlTypes.CHAR) @Column(length = 36) private UUID id;
  @JdbcTypeCode(SqlTypes.CHAR) @Column(name = "championship_id", nullable = false, length = 36) private UUID championshipId;
  @JdbcTypeCode(SqlTypes.CHAR) @Column(name = "match_id", nullable = false, length = 36) private UUID matchId;
  @Column(name = "team_index", nullable = false) private int teamIndex;
  @JdbcTypeCode(SqlTypes.CHAR) @Column(name = "player_id", nullable = false, length = 36) private UUID playerId;
  @Column(name = "card_type", nullable = false, length = 30) private String cardType;
  @Column(name = "match_minute") private Integer matchMinute;
  @Column(length = 500) private String reason;
  @Column(name = "created_at", nullable = false) private LocalDateTime createdAt = LocalDateTime.now();
}
