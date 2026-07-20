package com.boraver.teamgenerator.dto.teams;

import java.util.List;
import java.util.UUID;

public record SessionDetailResponse(
    UUID sessionId,
    String mode,
    String createdAt,
    List<GenerateTeamsResponse.Team> teams
) {}