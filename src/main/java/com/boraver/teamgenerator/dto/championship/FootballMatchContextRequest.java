package com.boraver.teamgenerator.dto.championship;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record FootballMatchContextRequest(
        @NotNull @Min(0) Integer homeScore,
        @NotNull @Min(0) Integer awayScore
) {}
