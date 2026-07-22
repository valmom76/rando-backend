package com.boraver.teamgenerator.dto.championship;

import com.boraver.teamgenerator.entity.SportType;
import java.time.LocalDateTime;
import java.util.UUID;

public record ChampionshipResponse(
    UUID id,
    String name,
    LocalDateTime createdAt,
    LocalDateTime startedAt,
    LocalDateTime endedAt,
    int teamCount,
    SportType sportType,
    String format,
    Integer groupsCount,
    Integer teamsPerGroup,
    Integer qualifiedPerGroup,
    String matchesType, // se aplicável
    String status,
    UUID generationSessionId,
    int defaultSetsToWin,
    int startersPerTeam,
    int yellowCardsForSuspension,
    int redCardSuspensionMatches
) {}
