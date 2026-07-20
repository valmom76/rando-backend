package com.boraver.teamgenerator.dto.match;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class SaveMatchResultRequest {
  @NotNull
  private Set<UUID> winningPlayerIds;
  @NotNull
  private Set<UUID> losingPlayerIds;
  private int teamScore;
  private int opponentScore;
  private UUID teamGenerationId;
}