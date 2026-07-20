package com.boraver.teamgenerator.dto.teams;

import lombok.Data;

import java.util.List;

@Data
public class TeamDTO {
  private int teamIndex;
  private double sumScore;
  private List<PlayerPickDTO> players;
}
