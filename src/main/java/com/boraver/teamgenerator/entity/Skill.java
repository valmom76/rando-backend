package com.boraver.teamgenerator.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "skill",
    uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "code"})
)
@Getter
@Setter
public class Skill {
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

  @Column(nullable=false)
  private boolean active = true;

  @Column(name="created_at", nullable=false)
  private OffsetDateTime createdAt = OffsetDateTime.now();
}
