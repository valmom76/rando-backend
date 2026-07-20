package com.boraver.teamgenerator.dto.teams;

import java.util.*;

public record GenerateTeamsResponse(
    UUID sessionId,
    List<Team> teams
) {
  public record Team(int teamIndex, double sumScore, List<PlayerPick> players) {}
  public record PlayerPick(UUID id, String name, String sex, double score) {}
}