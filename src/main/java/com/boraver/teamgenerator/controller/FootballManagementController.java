package com.boraver.teamgenerator.controller;

import com.boraver.teamgenerator.common.TenantContext;
import com.boraver.teamgenerator.dto.championship.FootballManagementDtos.*;
import com.boraver.teamgenerator.service.FootballManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class FootballManagementController {
  private final FootballManagementService service;

  @GetMapping("/football/referees")
  public List<RefereeResponse> listReferees() {
    return service.listReferees(tenantId());
  }

  @PostMapping("/football/referees")
  public RefereeResponse createReferee(@Valid @RequestBody CreateRefereeRequest request) {
    return service.createReferee(tenantId(), request);
  }

  @DeleteMapping("/football/referees/{refereeId}")
  public ResponseEntity<Void> deactivateReferee(@PathVariable UUID refereeId) {
    service.deactivateReferee(tenantId(), refereeId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/championships/{championshipId}/matches/{matchId}/football-management")
  public MatchManagementResponse getMatchManagement(
          @PathVariable UUID championshipId,
          @PathVariable UUID matchId) {
    return service.getMatchManagement(tenantId(), championshipId, matchId);
  }

  @PutMapping("/championships/{championshipId}/matches/{matchId}/lineup")
  public ResponseEntity<Void> saveLineup(
          @PathVariable UUID championshipId,
          @PathVariable UUID matchId,
          @Valid @RequestBody SaveLineupRequest request) {
    service.saveLineup(tenantId(), championshipId, matchId, request);
    return ResponseEntity.ok().build();
  }

  @PutMapping("/championships/{championshipId}/matches/{matchId}/officials")
  public ResponseEntity<Void> assignOfficials(
          @PathVariable UUID championshipId,
          @PathVariable UUID matchId,
          @Valid @RequestBody AssignOfficialsRequest request) {
    service.assignOfficials(tenantId(), championshipId, matchId, request);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/championships/{championshipId}/matches/{matchId}/substitutions")
  public SubstitutionResponse registerSubstitution(
          @PathVariable UUID championshipId,
          @PathVariable UUID matchId,
          @Valid @RequestBody SubstitutionRequest request) {
    return service.registerSubstitution(tenantId(), championshipId, matchId, request);
  }

  @PostMapping("/championships/{championshipId}/matches/{matchId}/cards")
  public CardResponse registerCard(
          @PathVariable UUID championshipId,
          @PathVariable UUID matchId,
          @Valid @RequestBody CardRequest request) {
    return service.registerCard(tenantId(), championshipId, matchId, request);
  }

  @PostMapping("/championships/{championshipId}/matches/{matchId}/goals")
  public GoalResponse registerGoal(
          @PathVariable UUID championshipId,
          @PathVariable UUID matchId,
          @Valid @RequestBody GoalRequest request) {
    return service.registerGoal(tenantId(), championshipId, matchId, request);
  }

  @DeleteMapping("/championships/{championshipId}/matches/{matchId}/goals/{goalId}")
  public ResponseEntity<Void> deleteGoal(
          @PathVariable UUID championshipId,
          @PathVariable UUID matchId,
          @PathVariable UUID goalId) {
    service.deleteGoal(tenantId(), championshipId, matchId, goalId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/championships/{championshipId}/scorers")
  public List<ScorerStandingResponse> getScorers(@PathVariable UUID championshipId) {
    return service.getScorers(tenantId(), championshipId);
  }

  @GetMapping("/championships/{championshipId}/discipline")
  public DisciplineResponse getDiscipline(@PathVariable UUID championshipId) {
    return service.getDiscipline(tenantId(), championshipId);
  }

  @PostMapping("/championships/{championshipId}/suspensions/{suspensionId}/appeals")
  public ResponseEntity<Void> createAppeal(
          @PathVariable UUID championshipId,
          @PathVariable UUID suspensionId,
          @Valid @RequestBody AppealRequest request) {
    service.createAppeal(tenantId(), championshipId, suspensionId, request);
    return ResponseEntity.ok().build();
  }

  @PutMapping("/championships/{championshipId}/appeals/{appealId}/decision")
  public ResponseEntity<Void> decideAppeal(
          @PathVariable UUID championshipId,
          @PathVariable UUID appealId,
          Authentication authentication,
          @Valid @RequestBody AppealDecisionRequest request) {
    String decidedBy = authentication != null ? authentication.getName() : "Comissão organizadora";
    service.decideAppeal(tenantId(), championshipId, appealId, decidedBy, request);
    return ResponseEntity.ok().build();
  }

  private UUID tenantId() {
    return UUID.fromString(TenantContext.getTenantId());
  }
}
