package com.boraver.teamgenerator.dto.championship;

public record StandingEntry(
    int teamIndex,
    Integer groupIndex,
    int points,
    int played,
    int wins,
    int draws,
    int losses,
    int goalsFor,
    int goalsAgainst,
    int goalsDifference,
    String teamName,
    int setsWon,
    int setsLost,
    int setsDifference
) {}