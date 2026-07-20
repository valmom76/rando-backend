package com.boraver.teamgenerator.dto.teams;

import java.time.LocalDateTime;
import java.util.UUID;

public record GeneratedTeamSessionSummary(UUID sessionId, LocalDateTime createdAt) {}