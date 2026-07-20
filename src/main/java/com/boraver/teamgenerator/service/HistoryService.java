package com.boraver.teamgenerator.service;

import com.boraver.teamgenerator.common.TenantContext;
import com.boraver.teamgenerator.dto.teams.*;
import com.boraver.teamgenerator.entity.*;
import com.boraver.teamgenerator.repository.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class HistoryService {

  private final TeamSessionRepository sessionRepo;
  private final GeneratedTeamRepository teamRepo;
  private final GeneratedTeamPlayerRepository teamPlayerRepo;
  private final PlayerRepository playerRepo;

  public HistoryService(TeamSessionRepository sessionRepo, GeneratedTeamRepository teamRepo,
                        GeneratedTeamPlayerRepository teamPlayerRepo, PlayerRepository playerRepo) {
    this.sessionRepo = sessionRepo;
    this.teamRepo = teamRepo;
    this.teamPlayerRepo = teamPlayerRepo;
    this.playerRepo = playerRepo;
  }

  private UUID tenantId() {
    return UUID.fromString(Objects.requireNonNull(TenantContext.getTenantId(), "tenant missing"));
  }

  public List<SessionSummaryResponse> listLatest() {
    UUID tenant = tenantId();
    return sessionRepo.findTop50ByTenantIdOrderByCreatedAtDesc(tenant).stream()
        .map(s -> new SessionSummaryResponse(
            s.getId(),
            s.getMode(),
            s.getCreatedAt().toString(),
            s.getTeamCount(),
            s.getPlayersPerTeam(),
            s.getPlayersCount()
        ))
        .toList();
  }

  public SessionDetailResponse detail(UUID sessionId) {
    UUID tenant = tenantId();

    TeamGenerationSession session = sessionRepo.findByIdAndTenantId(sessionId, tenant)
        .orElseThrow(() -> new IllegalArgumentException("Session not found"));

    List<GeneratedTeam> teams = teamRepo.findAllBySessionIdOrderByTeamIndexAsc(sessionId);
    List<UUID> teamIds = teams.stream().map(GeneratedTeam::getId).toList();

    List<GeneratedTeamPlayer> picks = teamPlayerRepo.findAllByTeamIds(teamIds);
    Map<UUID, List<GeneratedTeamPlayer>> byTeam = picks.stream().collect(Collectors.groupingBy(GeneratedTeamPlayer::getTeamId));

    // carregar nomes dos players
    Set<UUID> playerIds = picks.stream().map(GeneratedTeamPlayer::getPlayerId).collect(Collectors.toSet());
    Map<UUID, Player> playerMap = playerRepo.findAllByTenantIdAndIdIn(tenant, new ArrayList<>(playerIds))
        .stream().collect(Collectors.toMap(Player::getId, p -> p));

    List<GenerateTeamsResponse.Team> respTeams = new ArrayList<>();
    for (var t : teams) {
      var teamPicks = byTeam.getOrDefault(t.getId(), List.of());
      double sumScore = teamPicks.stream().map(tp -> tp.getScoreAtGeneration().doubleValue()).mapToDouble(d -> d).sum();

      List<GenerateTeamsResponse.PlayerPick> players = teamPicks.stream().map(tp -> {
        Player p = playerMap.get(tp.getPlayerId());
        String name = p != null ? p.getName() : "Unknown";
        String sex = p != null ? String.valueOf(p.getSex()) : tp.getSexAtGeneration();
        return new GenerateTeamsResponse.PlayerPick(tp.getPlayerId(), name, sex, tp.getScoreAtGeneration().doubleValue());
      }).toList();

      respTeams.add(new GenerateTeamsResponse.Team(t.getTeamIndex(), sumScore, players));
    }

    return new SessionDetailResponse(session.getId(), session.getMode(), session.getCreatedAt().toString(), respTeams);
  }
}

