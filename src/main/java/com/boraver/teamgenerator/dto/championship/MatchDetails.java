package com.boraver.teamgenerator.dto.championship;

import java.util.UUID;

public record MatchDetails(
    UUID matchId,
    Integer groupIndex,
    int round,
    int homeTeamIndex,
    int awayTeamIndex,
    Integer homeScore,
    Integer awayScore,
    boolean played,
    Integer winnerTeamIndex,
    UUID generationSessionId,
    String stage,
    String homeTeamName,
    String awayTeamName,
    int setsToWin,
    int pointsPerSet,
    int tieBreakPoints,
    int homeSetsWon,
    int awaySetsWon,
    Integer homePenaltyScore,
    Integer awayPenaltyScore
) {}
