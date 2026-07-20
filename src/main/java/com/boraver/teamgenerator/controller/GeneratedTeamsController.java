package com.boraver.teamgenerator.controller;

import com.boraver.teamgenerator.common.TenantContext;
import com.boraver.teamgenerator.dto.teams.GeneratedTeamSessionSummary;
import com.boraver.teamgenerator.dto.teams.SaveGeneratedTeamsRequest;
import com.boraver.teamgenerator.service.GeneratedTeamsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/generated-teams")
@RequiredArgsConstructor
public class GeneratedTeamsController {

  private final GeneratedTeamsService service;
  private final ObjectMapper objectMapper;

  @PostMapping
  public void saveTeams(@RequestBody SaveGeneratedTeamsRequest request, Authentication auth) {
    UUID tenantId = UUID.fromString(TenantContext.getTenantId());
    service.saveTeams(tenantId, request);
  }

  @GetMapping("/latest")
  public SaveGeneratedTeamsRequest getLatestTeams(Authentication auth) {
    UUID tenantId = UUID.fromString(TenantContext.getTenantId());
    return service.getLatestTeams(tenantId);
  }

  @PostMapping("/end-day")
  public void endDay(Authentication auth) {
    UUID tenantId = UUID.fromString(TenantContext.getTenantId());
    service.endDay(tenantId);
  }

  @GetMapping("/sessions")
  public List<GeneratedTeamSessionSummary> listSessions(Authentication auth) {
    UUID tenantId = UUID.fromString(TenantContext.getTenantId());
    return service.listSessions(tenantId);
  }
}