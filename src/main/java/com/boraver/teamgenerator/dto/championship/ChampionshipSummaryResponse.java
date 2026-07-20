package com.boraver.teamgenerator.dto.championship;

import com.boraver.teamgenerator.entity.SportType;
import java.time.LocalDateTime;
import java.util.UUID;

public record ChampionshipSummaryResponse(
    UUID id,
    String name,
    LocalDateTime createdAt,
    LocalDateTime startedAt,
    LocalDateTime endedAt,
    SportType sportType,
    int teamCount,
    int groupsCount,
    String status
) {}
