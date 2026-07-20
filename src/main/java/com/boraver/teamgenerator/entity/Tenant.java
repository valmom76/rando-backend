package com.boraver.teamgenerator.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "tenant",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"slug"})
    })
public class Tenant {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @JdbcTypeCode(SqlTypes.CHAR)
  @Column(length = 36)
  private UUID id;

  @Column(nullable = false, length = 120)
  private String name;

  @Column(nullable = false, unique = true, length = 80)
  private String slug;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt = OffsetDateTime.now();

  @Column(name = "logo_url", length = 255)
  private String logoUrl;

  @Column(name = "primary_color", nullable = false, length = 7)
  private String primaryColor = "#2bd96b";

  @Column(name = "secondary_color", nullable = false, length = 7)
  private String secondaryColor = "#1a1a1a";

  @Enumerated(EnumType.STRING)
  @Column(name = "sport_type", nullable = false, length = 20)
  private SportType sportType = SportType.VOLLEYBALL;
}
