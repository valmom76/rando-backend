package com.boraver.teamgenerator.controller;

import com.boraver.teamgenerator.dto.championship.*;
import com.boraver.teamgenerator.service.ChampionshipService;
import com.boraver.teamgenerator.common.TenantContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/championships")
@RequiredArgsConstructor
public class ChampionshipController {

  private final ChampionshipService championshipService;

  @PostMapping
  public ResponseEntity<ChampionshipResponse> create(
          @Valid @RequestBody CreateChampionshipRequest request,
          Authentication auth
  ) {
    UUID tenantId = UUID.fromString(TenantContext.getTenantId());
    ChampionshipResponse response = championshipService.createChampionship(tenantId, request);
    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<List<ChampionshipSummary>> listChampionships(Authentication auth) {
    UUID tenantId = UUID.fromString(TenantContext.getTenantId());
    List<ChampionshipSummary> championships = championshipService.listChampionships(tenantId);
    return ResponseEntity.ok(championships);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ChampionshipDetails> getChampionshipDetails(@PathVariable UUID id, Authentication auth) {
    UUID tenantId = UUID.fromString(TenantContext.getTenantId());
    ChampionshipDetails details = championshipService.getChampionshipDetails(id, tenantId);
    return ResponseEntity.ok(details);
  }

  @GetMapping("/{id}/groups/{groupIndex}/standings")
  public ResponseEntity<List<StandingEntry>> getGroupStandings(
          @PathVariable UUID id,
          @PathVariable int groupIndex
  ) {
    List<StandingEntry> standings = championshipService.getGroupStandings(id, groupIndex);
    return ResponseEntity.ok(standings);
  }

  @GetMapping("/{id}/groups/{groupIndex}/matches")
  public ResponseEntity<List<MatchDetails>> getGroupMatches(
          @PathVariable UUID id,
          @PathVariable int groupIndex
  ) {
    List<MatchDetails> matches = championshipService.getGroupMatches(id, groupIndex);
    return ResponseEntity.ok(matches);
  }

  @GetMapping("/{id}/matches")
  public ResponseEntity<List<MatchDetails>> getAllMatches(@PathVariable UUID id) {
    List<MatchDetails> matches = championshipService.getAllMatches(id);
    return ResponseEntity.ok(matches);
  }

  @PostMapping("/{championshipId}/matches/{matchId}/start")
  public ResponseEntity<MatchStartResponse> startMatch(
          @PathVariable UUID championshipId,
          @PathVariable UUID matchId
  ) {
    MatchStartResponse response = championshipService.startMatch(championshipId, matchId);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/{championshipId}/matches/result")
  public ResponseEntity<Void> registerMatchResult(
          @PathVariable UUID championshipId,
          @Valid @RequestBody MatchResultRequest request
  ) {
    championshipService.registerMatchResult(championshipId, request);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/{championshipId}/matches/{matchId}/football-context")
  public ResponseEntity<FootballMatchContextResponse> getFootballMatchContext(
          @PathVariable UUID championshipId,
          @PathVariable UUID matchId,
          @Valid @RequestBody FootballMatchContextRequest request
  ) {
    return ResponseEntity.ok(
            championshipService.getFootballMatchContext(championshipId, matchId, request));
  }

  @GetMapping("/{championshipId}/matches/{matchId}")
  public ResponseEntity<MatchDetails> getMatchDetails(
          @PathVariable UUID championshipId,
          @PathVariable UUID matchId
  ) {
    MatchDetails details = championshipService.getMatchDetails(championshipId, matchId);
    return ResponseEntity.ok(details);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteChampionship(@PathVariable UUID id, Authentication auth) {
    UUID tenantId = UUID.fromString(TenantContext.getTenantId());
    championshipService.deleteChampionship(id, tenantId);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{championshipId}/knockout/next")
  public ResponseEntity<Void> generateNextKnockoutStage(@PathVariable UUID championshipId) {
    championshipService.generateNextKnockoutStage(championshipId);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/{championshipId}/knockout/third-place")
  public ResponseEntity<Void> generateThirdPlace(@PathVariable UUID championshipId) {
    championshipService.generateThirdPlaceMatch(championshipId);
    return ResponseEntity.ok().build();
  }
}
