package com.boraver.teamgenerator.dto.teams;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record SaveGeneratedRequest(
    @NotEmpty List<TeamDTO> teams
) {
  public record TeamDTO(
      int teamIndex,
      @NotEmpty List<PlayerPickDTO> players
  ) {}

  public record PlayerPickDTO(
      String id,
      String name,
      String sex,
      double score
  ) {}
}