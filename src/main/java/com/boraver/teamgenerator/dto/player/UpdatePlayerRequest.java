package com.boraver.teamgenerator.dto.player;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record UpdatePlayerRequest(
        String name,
        String sex,
        String email,
        String phone,
        LocalDate birthDate,
        Boolean active,
        List<PositionAssignment> positions    // NOVO – se null, não altera; se vazio, remove todas
) {
  public record PositionAssignment(
          @NotBlank UUID positionId,
          int priority
  ) {}
}