package com.boraver.teamgenerator.dto.teams;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record SaveManualTeamsRequest(
    @NotEmpty String name,
    int groupsCount,
    String matchesType,
    int qualifiedPerGroup,
    @NotEmpty List<ManualTeamDTO> teams,
    Map<Integer, String> teamNames,
    int setsToWin,
    int pointsPerSet,
    int tieBreakPoints,
    String format
) {
  public record ManualTeamDTO(
      int teamIndex,
      @NotEmpty List<UUID> playerIds,
      int groupId
  ) {}
}