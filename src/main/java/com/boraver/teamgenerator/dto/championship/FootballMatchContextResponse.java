package com.boraver.teamgenerator.dto.championship;

public record FootballMatchContextResponse(
        boolean knockout,
        boolean twoLegged,
        boolean decidingLeg,
        int aggregateHomeScore,
        int aggregateAwayScore,
        boolean penaltiesRequired
) {}
