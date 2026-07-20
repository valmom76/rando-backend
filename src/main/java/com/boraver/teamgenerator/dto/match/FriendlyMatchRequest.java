package com.boraver.teamgenerator.dto.match;

import jakarta.validation.constraints.NotNull;

public record FriendlyMatchRequest(
        @NotNull String courtName,
        @NotNull Integer homeTeamIndex,
        @NotNull Integer awayTeamIndex,
        int homeScore,
        int awayScore,
        Boolean walkover,
        Integer winnerTeamIndex
) {}