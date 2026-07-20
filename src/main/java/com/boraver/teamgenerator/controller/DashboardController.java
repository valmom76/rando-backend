package com.boraver.teamgenerator.controller;

import com.boraver.teamgenerator.common.TenantContext;
import com.boraver.teamgenerator.dto.player.InactivePlayerDTO;
import com.boraver.teamgenerator.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

  private final ChampionshipService championshipService;
  private final PlayerService playerService;
  private final SkillService skillService;
  private final TeamGenerationService teamGenerationService; // nome correto

  @GetMapping("/stats")
  public Map<String, Object> getStats() {
    UUID tenantId = UUID.fromString(TenantContext.getTenantId());
    Map<String, Object> stats = new HashMap<>();
    stats.put("totalChampionships", championshipService.countByTenant(tenantId));
    stats.put("activeChampionships", championshipService.countByStatus(tenantId, "CREATED"));
    stats.put("finishedChampionships", championshipService.countByStatus(tenantId, "FINISHED"));
    stats.put("totalPlayers", playerService.countActive(tenantId));
    stats.put("totalSkills", skillService.countActive(tenantId));
    stats.put("totalTeams", teamGenerationService.countTeamsByTenant(tenantId));
    // stats.put("totalMatches", championshipService.countMatches(tenantId)); // se implementar
    return stats;
  }

  @GetMapping("/inactive-players")
  public List<InactivePlayerDTO> getInactivePlayers() {
    UUID tenantId = UUID.fromString(TenantContext.getTenantId());
    return playerService.getInactivePlayers(tenantId);
  }
}