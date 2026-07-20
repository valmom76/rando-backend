package com.boraver.teamgenerator.dto.championship;

import java.util.List;
import java.util.UUID;

public record MatchStartResponse(
    UUID matchId,
    int homeTeamIndex,
    int awayTeamIndex,
    List<PlayerInfo> homeTeamPlayers,
    List<PlayerInfo> awayTeamPlayers
) {
  public record PlayerInfo(UUID id, String name, char sex) {}
}