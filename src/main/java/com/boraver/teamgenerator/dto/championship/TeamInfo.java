package com.boraver.teamgenerator.dto.championship;

public record TeamInfo(
    int teamIndex,
    Integer groupIndex,
    int seed,
    java.math.BigDecimal initialScore
) {}