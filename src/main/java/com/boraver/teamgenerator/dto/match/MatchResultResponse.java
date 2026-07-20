package com.boraver.teamgenerator.dto.match;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class MatchResultResponse {
  private UUID id;
  private LocalDateTime createdAt;
  private Set<UUID> winningPlayerIds;
  private Set<UUID> losingPlayerIds;
  private int teamScore;
  private int opponentScore;
}