package com.boraver.teamgenerator.dto.teams;

import lombok.Data;

import java.util.UUID;

@Data
public class PlayerPickDTO {
  private UUID id;
  private String name;
  private String sex;
  private double score;
}
