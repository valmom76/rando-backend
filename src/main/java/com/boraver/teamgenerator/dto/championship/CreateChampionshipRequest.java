package com.boraver.teamgenerator.dto.championship;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.UUID;

public record CreateChampionshipRequest(
    @NotBlank String name,
    @NotNull UUID generationSessionId,
    String format, // "GROUPS", "KNOCKOUT", "LEAGUE"
    String matchesType,
    Integer groupsCount, // usado se format = "GROUPS"
    Integer teamsPerGroup, // pode ser calculado ou informado? Normalmente é automático
    Integer qualifiedPerGroup, // usado se format = "GROUPS"
    Map<Integer, String> teamNames,
    int setsToWin,
    int pointsPerSet,
    int tieBreakPoints
) {}