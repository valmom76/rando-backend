package com.boraver.teamgenerator.dto.championship;

import com.boraver.teamgenerator.entity.SportType;
import java.time.LocalDateTime;
import java.util.UUID;

public record ChampionshipSummary(
    UUID id,
    String name,
    LocalDateTime createdAt,
    String status,
    SportType sportType,
    int teamCount,
    int groupsCount
) {}
