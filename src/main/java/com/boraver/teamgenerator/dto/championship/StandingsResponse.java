package com.boraver.teamgenerator.dto.championship;

import java.util.List;

public record StandingsResponse(
    List<GroupStandings> groups
) {
  public record GroupStandings(
      int groupIndex,
      List<TeamStanding> standings
  ) {}
  public record TeamStanding(
      int teamIndex,
      int points,
      int played,
      int wins,
      int draws,
      int losses,
      int goalsFor,
      int goalsAgainst,
      int goalsDifference
  ) {}
}