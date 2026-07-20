package com.boraver.teamgenerator.dto.teams;

import java.util.*;

public record GenerateTeamsRequest(
    List<UUID> playerIds,
    int teamCount,
    int playersPerTeam,
    List<SelectedSkill> selectedSkills,
    Map<String, Double> sexMultiplier,
    SexBalance sexBalance,
    String extrasPolicy,
    List<UUID> requiredPositions
) {
  public record SelectedSkill(UUID skillId, double weight) {}
  public record SexBalance(boolean enabled, int maxMaleDiff) {}
}
