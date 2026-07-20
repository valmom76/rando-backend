package com.boraver.teamgenerator.dto.rating;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record UpsertRatingsRequest(
    @NotNull UUID playerId,
    @NotEmpty List<SkillRating> ratings
) {
  public record SkillRating(
      @NotNull UUID skillId,
      @Min(0) @Max(5) int rating
  ) {}
}