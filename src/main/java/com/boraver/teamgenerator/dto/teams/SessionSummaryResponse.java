package com.boraver.teamgenerator.dto.teams;

import java.util.UUID;

public record SessionSummaryResponse(
    UUID sessionId,
    String mode,
    String createdAt,
    int teamCount,
    int playersPerTeam,
    int playersCount
) {}
