package com.boraver.teamgenerator.dto.match;

import java.util.List;
import java.util.UUID;

public record ActiveSessionDTO(
        UUID sessionId,
        String dateFormatted,
        List<CourtDTO> courts
) {
  public record CourtDTO(
          String name,
          List<TeamInfo> teams
  ) {}

  public record TeamInfo(
          int teamIndex,
          String teamName,
          double avgRating,
          int womenCount,
          List<String> playerNames
  ) {}
}