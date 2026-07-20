package com.boraver.teamgenerator.dto.game;

import java.util.List;

public record DistributionSuggestion(
        List<CourtAllocation> courts
) {
  public record CourtAllocation(
          String name,
          List<TeamInfo> teams
  ) {}

  public record TeamInfo(
          int teamIndex,
          String teamName,
          double avgRating,
          int womenCount
  ) {}
}