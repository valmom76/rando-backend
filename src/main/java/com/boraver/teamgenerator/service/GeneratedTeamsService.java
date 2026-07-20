package com.boraver.teamgenerator.service;

import com.boraver.teamgenerator.dto.teams.GeneratedTeamSessionSummary;
import com.boraver.teamgenerator.dto.teams.SaveGeneratedTeamsRequest;
import com.boraver.teamgenerator.entity.GeneratedTeams;
import com.boraver.teamgenerator.repository.ChampionshipRepository;
import com.boraver.teamgenerator.repository.GeneratedTeamsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GeneratedTeamsService {

  private final GeneratedTeamsRepository repository;
  private final ChampionshipRepository championshipRepository;
  private final ObjectMapper objectMapper;

  @Transactional
  public void saveTeams(UUID tenantId, SaveGeneratedTeamsRequest request) {
    // Desativa a geração anterior
    repository.deactivatePreviousGenerations(tenantId);

    // Cria nova geração
    GeneratedTeams teams = new GeneratedTeams();
    teams.setTenantId(tenantId);
    try {
      teams.setTeamsJson(objectMapper.writeValueAsString(request));
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Erro ao serializar times", e);
    }
    teams.setActive(true);
    repository.save(teams);
  }

  public SaveGeneratedTeamsRequest getLatestTeams(UUID tenantId) {
    return repository.findTopByTenantIdAndActiveTrueOrderByCreatedAtDesc(tenantId)
        .map(teams -> {
          try {
            return objectMapper.readValue(teams.getTeamsJson(), SaveGeneratedTeamsRequest.class);
          } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao desserializar times", e);
          }
        })
        .orElse(null);
  }

  @Transactional
  public void endDay(UUID tenantId) {
    repository.deactivatePreviousGenerations(tenantId);
  }

  public List<GeneratedTeamSessionSummary> listSessions(UUID tenantId) {
    return repository.findTopByTenantIdAndActiveTrueOrderByCreatedAtDesc(tenantId)
        .stream()
        .map(gt -> new GeneratedTeamSessionSummary(gt.getId(), gt.getCreatedAt()))
        .collect(Collectors.toList());
  }

  public long countByTenant(UUID tenantId) {
    return championshipRepository.countByTenantId(tenantId);
  }

  public long countByStatus(UUID tenantId, String status) {
    return championshipRepository.countByTenantIdAndStatus(tenantId, status);
  }
}