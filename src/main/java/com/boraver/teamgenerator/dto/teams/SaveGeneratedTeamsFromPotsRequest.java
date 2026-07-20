package com.boraver.teamgenerator.dto.teams;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class SaveGeneratedTeamsFromPotsRequest {
  @NotNull
  private List<TeamDTO> teams;
  private String mode;
  @Min(1)
  private int teamCount;
  @Min(1)
  private int playersPerTeam;

  // getters e setters

  public static class TeamDTO {
    private int teamIndex;
    private double sumScore;
    private List<PlayerPickDTO> players;
    // getters e setters
  }

  public static class PlayerPickDTO {
    private String id; // UUID como string
    private String name;
    private String sex;
    private double score;
    // getters e setters
  }
}