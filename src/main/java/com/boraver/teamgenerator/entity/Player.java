package com.boraver.teamgenerator.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="player")
@Getter
@Setter
public class Player {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @JdbcTypeCode(SqlTypes.CHAR)
  @Column(length = 36)
  private UUID id;

  @Column(name = "tenant_id", nullable = false)
  @JdbcTypeCode(SqlTypes.CHAR)
  private UUID tenantId;

  @Column(nullable=false, length=120)
  private String name;

  @Column(nullable=false, length=1)
  private char sex; // M/F

  @Column(length = 255)
  private String email;

  @Column(length = 20)
  private String phone;

  @Column(name = "birth_date")
  private LocalDate birthDate;

  @Column(nullable=false)
  private boolean active = true;

  @Column(name="created_at", nullable=false)
  private OffsetDateTime createdAt = OffsetDateTime.now();

  @OneToMany(mappedBy = "playerId", fetch = FetchType.LAZY)
  private List<PlayerPosition> positions;
}
