package com.boraver.teamgenerator.dto.teams;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public class PotSelectionRequest {
  @NotNull
  private List<List<UUID>> potes; // Cada sublista representa um pote com os IDs dos jogadores

  @Min(1)
  private int teamCount;

  public List<List<UUID>> getPotes() { return potes; }
  public void setPotes(List<List<UUID>> potes) { this.potes = potes; }
  public int getTeamCount() { return teamCount; }
  public void setTeamCount(int teamCount) { this.teamCount = teamCount; }
}