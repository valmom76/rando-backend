package com.boraver.teamgenerator.controller;

import com.boraver.teamgenerator.dto.rating.*;
import com.boraver.teamgenerator.service.RatingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ratings")
public class RatingController {

  private final RatingService service;

  public RatingController(RatingService service) { this.service = service; }

  @PutMapping("/upsert")
  public Map<String, String> upsert(@Valid @RequestBody UpsertRatingsRequest req) {
    service.upsert(req);
    return Map.of("status", "ok");
  }

  @GetMapping("/player/{playerId}/history")
  public List<PlayerRatingResponse> history(@PathVariable UUID playerId) {
    var all = service.listAllPlayerRatings(playerId);
    return all.stream()
        .map(r -> new PlayerRatingResponse(
            r.getSkillId(),
            r.getRating(),
            r.getValidTo() == null
        ))
        .toList();
  }

  @GetMapping("/player/{playerId}/current")
  public Map<UUID, Integer> current(@PathVariable UUID playerId) {
    return service.currentRatingsMap(playerId);
  }
}
