package com.boraver.teamgenerator.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name="generated_team")
@Getter
@Setter
public class GeneratedTeam {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @JdbcTypeCode(SqlTypes.CHAR)
  @Column(length = 36)
  private UUID id;

  @Column(name="session_id", nullable=false)
  @JdbcTypeCode(SqlTypes.CHAR)
  private UUID sessionId;

  @Column(name="team_index", nullable=false)
  private int teamIndex;

  @Column(nullable=false, length=120)
  private String name;
}

