package com.boraver.teamgenerator.dto.championship;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public final class FootballManagementDtos {
  private FootballManagementDtos() {}

  public record CreateRefereeRequest(@NotBlank String name) {}
  public record RefereeResponse(UUID id, String name) {}

  public record AssignOfficialsRequest(
          @NotNull UUID mainRefereeId,
          UUID assistantOneId,
          UUID assistantTwoId
  ) {}

  public record OfficialResponse(String role, UUID refereeId, String refereeName) {}

  public record TeamLineupRequest(
          int teamIndex,
          @NotNull List<UUID> starters,
          @NotNull List<UUID> reserves
  ) {}

  public record SaveLineupRequest(
          @NotNull @Valid TeamLineupRequest home,
          @NotNull @Valid TeamLineupRequest away
  ) {}

  public record SubstitutionRequest(
          int teamIndex,
          @NotNull UUID playerOutId,
          @NotNull UUID playerInId,
          @Min(0) Integer minute
  ) {}

  public record CardRequest(
          int teamIndex,
          @NotNull UUID playerId,
          @NotBlank String cardType,
          @Min(0) Integer minute,
          String reason
  ) {}

  public record GoalRequest(
          int scoringTeamIndex,
          @NotNull UUID playerId,
          boolean ownGoal,
          @Min(0) Integer minute
  ) {}

  public record AppealRequest(@NotBlank String reason) {}
  public record AppealDecisionRequest(boolean accepted, @NotBlank String notes) {}

  public record MatchPlayerResponse(
          UUID playerId,
          String playerName,
          int teamIndex,
          String rosterRole,
          boolean suspended,
          Integer suspensionMatchesRemaining
  ) {}

  public record CardResponse(
          UUID id,
          UUID playerId,
          String playerName,
          int teamIndex,
          String cardType,
          Integer minute,
          String reason,
          LocalDateTime createdAt
  ) {}

  public record SubstitutionResponse(
          UUID id,
          int teamIndex,
          UUID playerOutId,
          String playerOutName,
          UUID playerInId,
          String playerInName,
          Integer minute
  ) {}

  public record GoalResponse(
          UUID id,
          int scoringTeamIndex,
          int scorerTeamIndex,
          UUID playerId,
          String playerName,
          boolean ownGoal,
          Integer minute,
          LocalDateTime createdAt
  ) {}

  public record MatchManagementResponse(
          int startersPerTeam,
          int yellowCardsForSuspension,
          int redCardSuspensionMatches,
          List<MatchPlayerResponse> players,
          List<OfficialResponse> officials,
          List<CardResponse> cards,
          List<SubstitutionResponse> substitutions,
          List<GoalResponse> goals
  ) {}

  public record ScorerStandingResponse(
          UUID playerId,
          String playerName,
          int teamIndex,
          String teamName,
          long goals,
          long ownGoals
  ) {}

  public record SuspensionResponse(
          UUID id,
          UUID playerId,
          String playerName,
          int teamIndex,
          String teamName,
          String reason,
          int totalMatches,
          int remainingMatches,
          String status,
          UUID sourceMatchId,
          UUID appealId,
          String appealStatus,
          String appealReason,
          String decisionNotes,
          String decidedBy,
          LocalDateTime decidedAt
  ) {}

  public record DisciplineResponse(List<SuspensionResponse> suspensions) {}
}
