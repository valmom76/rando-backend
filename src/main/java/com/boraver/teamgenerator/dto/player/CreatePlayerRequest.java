package com.boraver.teamgenerator.dto.player;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreatePlayerRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Pattern(regexp = "[MF]") String sex,
        @Size(max = 255) String email,
        @Size(max = 30) String phone,
        LocalDate birthDate,
        List<PositionAssignment> positions     // NOVO
) {
  public record PositionAssignment(
          @NotBlank UUID positionId,
          int priority
  ) {}
}