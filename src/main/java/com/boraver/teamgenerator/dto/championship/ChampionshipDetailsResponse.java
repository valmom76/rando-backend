package com.boraver.teamgenerator.dto.championship;

import com.boraver.teamgenerator.entity.ChampionshipStandings;
import com.boraver.teamgenerator.entity.SportType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ChampionshipDetailsResponse(
    UUID id,
    String name,
    LocalDateTime createdAt,
    LocalDateTime startedAt,
    LocalDateTime endedAt,
    SportType sportType,
    int teamCount,
    int groupsCount,
    int teamsPerGroup,
    int qualifiedPerGroup,
    String matchesType,
    String status,
    List<GroupDetails> groups,
    List<MatchDetails> matches
) {
  public record GroupDetails(
      int groupIndex,
      List<TeamInfo> teams
  ) {}
  public record TeamInfo(
      int teamIndex,
      int seed,
      double initialScore,
      ChampionshipStandings standings // opcional, podemos incluir
  ) {}
  public record MatchDetails(
      UUID matchId,
      int round,
      int homeTeamIndex,
      int awayTeamIndex,
      Integer homeScore,
      Integer awayScore,
      boolean played,
      Integer winnerTeamIndex
  ) {}
}
