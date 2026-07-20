package com.boraver.teamgenerator.dto.teams;

import java.util.UUID;

public record SaveManualTeamsResponse(UUID sessionId, UUID championshipId) {}