package com.boraver.teamgenerator.dto.player;

import java.time.LocalDateTime;
import java.util.List;

public record PlayerEvolutionDTO(
        String skillName,
        List<EvolutionPoint> points
) {
  public record EvolutionPoint(
          LocalDateTime date,
          int rating
  ) {}
}