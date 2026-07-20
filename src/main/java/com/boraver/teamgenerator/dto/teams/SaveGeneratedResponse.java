package com.boraver.teamgenerator.dto.teams;

import java.util.List;
import java.util.UUID;

public record SaveGeneratedResponse(
    UUID sessionId,
    List<GenerateTeamsResponse.Team> teams
) {}