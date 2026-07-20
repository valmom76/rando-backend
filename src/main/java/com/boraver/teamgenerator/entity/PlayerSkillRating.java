package com.boraver.teamgenerator.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name="player_skill_rating")
public class PlayerSkillRating {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @JdbcTypeCode(SqlTypes.CHAR)
  @Column(length = 36)
  private UUID id;

  @Column(name = "tenant_id", nullable = false)
  @JdbcTypeCode(SqlTypes.CHAR)
  private UUID tenantId;

  @Column(name="player_id", nullable=false)
  @JdbcTypeCode(SqlTypes.CHAR)
  private UUID playerId;

  @Column(name="skill_id", nullable=false)
  @JdbcTypeCode(SqlTypes.CHAR)
  private UUID skillId;

  @Column(nullable=false)
  private short rating; // 0-5

  @Column(name="valid_from", nullable=false)
  private OffsetDateTime validFrom = OffsetDateTime.now();

  @Column(name="valid_to")
  private OffsetDateTime validTo;

  public OffsetDateTime getValidFrom() {
    return validFrom;
  }

  public void setValidFrom(OffsetDateTime validFrom) {
    this.validFrom = validFrom;
  }

  public UUID getId() { return id; }
  public UUID getTenantId() { return tenantId; }
  public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
  public UUID getPlayerId() { return playerId; }
  public void setPlayerId(UUID playerId) { this.playerId = playerId; }
  public UUID getSkillId() { return skillId; }
  public void setSkillId(UUID skillId) { this.skillId = skillId; }
  public short getRating() { return rating; }
  public void setRating(short rating) { this.rating = rating; }
  public OffsetDateTime getValidTo() { return validTo; }
  public void setValidTo(OffsetDateTime validTo) { this.validTo = validTo; }
}