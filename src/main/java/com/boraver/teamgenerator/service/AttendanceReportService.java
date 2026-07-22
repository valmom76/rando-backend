package com.boraver.teamgenerator.service;

import com.boraver.teamgenerator.dto.report.AttendanceDtos.AttendanceReportResponse;
import com.boraver.teamgenerator.dto.report.AttendanceDtos.PlayerAttendance;
import com.boraver.teamgenerator.dto.report.AttendanceDtos.PlayerOption;
import com.boraver.teamgenerator.dto.report.AttendanceDtos.SessionAttendanceResponse;
import com.boraver.teamgenerator.dto.report.AttendanceDtos.UpdateSessionAttendanceRequest;
import com.boraver.teamgenerator.entity.FriendlySessionAttendance;
import com.boraver.teamgenerator.entity.FriendlySessionAttendanceConfirmation;
import com.boraver.teamgenerator.entity.GeneratedTeam;
import com.boraver.teamgenerator.entity.GeneratedTeamPlayer;
import com.boraver.teamgenerator.entity.Player;
import com.boraver.teamgenerator.entity.TeamGenerationSession;
import com.boraver.teamgenerator.repository.FriendlySessionAttendanceConfirmationRepository;
import com.boraver.teamgenerator.repository.FriendlySessionAttendanceRepository;
import com.boraver.teamgenerator.repository.ChampionshipRepository;
import com.boraver.teamgenerator.repository.GeneratedTeamPlayerRepository;
import com.boraver.teamgenerator.repository.GeneratedTeamRepository;
import com.boraver.teamgenerator.repository.PlayerRepository;
import com.boraver.teamgenerator.repository.TeamGenerationSessionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceReportService {

  private static final ZoneId GROUP_ZONE = ZoneId.of("America/Fortaleza");

  private final TeamGenerationSessionRepository sessionRepository;
  private final GeneratedTeamRepository generatedTeamRepository;
  private final GeneratedTeamPlayerRepository generatedTeamPlayerRepository;
  private final PlayerRepository playerRepository;
  private final FriendlySessionAttendanceRepository attendanceRepository;
  private final FriendlySessionAttendanceConfirmationRepository confirmationRepository;
  private final ChampionshipRepository championshipRepository;
  private final ObjectMapper mapper;

  @Transactional(readOnly = true)
  public SessionAttendanceResponse getSessionAttendance(UUID tenantId, UUID sessionId) {
    TeamGenerationSession session = requireAttendanceEligibleSession(tenantId, sessionId);
    Set<UUID> suggestedPlayers = generatedPlayerIds(sessionId);
    Set<UUID> attendedPlayers = attendanceRepository
            .findBySessionIdAndTenantId(sessionId, tenantId)
            .stream()
            .map(FriendlySessionAttendance::getPlayerId)
            .collect(Collectors.toSet());
    Optional<FriendlySessionAttendanceConfirmation> confirmation = confirmationRepository
            .findBySessionIdAndTenantId(sessionId, tenantId);

    List<PlayerOption> players = playerRepository
            .findAllByTenantIdOrderByNameAsc(tenantId)
            .stream()
            .map(player -> new PlayerOption(
                    player.getId(),
                    player.getName(),
                    player.isActive(),
                    suggestedPlayers.contains(player.getId()),
                    attendedPlayers.contains(player.getId())))
            .toList();

    return new SessionAttendanceResponse(
            sessionId,
            resolveSessionDate(session),
            confirmation.isPresent(),
            confirmation.map(FriendlySessionAttendanceConfirmation::getConfirmedAt).orElse(null),
            confirmation.map(FriendlySessionAttendanceConfirmation::getUpdatedAt).orElse(null),
            players);
  }

  @Transactional
  public SessionAttendanceResponse confirmSessionAttendance(
          UUID tenantId,
          UUID userId,
          UUID sessionId,
          UpdateSessionAttendanceRequest request) {
    TeamGenerationSession session = requireAttendanceEligibleSession(tenantId, sessionId);
    Set<UUID> playerIds = new LinkedHashSet<>(request.playerIds());
    if (playerIds.isEmpty()) {
      throw new IllegalArgumentException("Selecione ao menos um jogador presente.");
    }

    List<Player> tenantPlayers = playerRepository
            .findAllByTenantIdAndIdIn(tenantId, new ArrayList<>(playerIds));
    if (tenantPlayers.size() != playerIds.size()) {
      throw new IllegalArgumentException(
              "A lista contém um jogador que não pertence a este grupo.");
    }

    attendanceRepository.deleteBySessionIdAndTenantId(sessionId, tenantId);
    LocalDateTime now = LocalDateTime.now();
    List<FriendlySessionAttendance> attendances = playerIds.stream()
            .map(playerId -> newAttendance(tenantId, sessionId, playerId, now))
            .toList();
    attendanceRepository.saveAll(attendances);

    FriendlySessionAttendanceConfirmation confirmation = confirmationRepository
            .findBySessionIdAndTenantId(sessionId, tenantId)
            .orElseGet(() -> {
              FriendlySessionAttendanceConfirmation created =
                      new FriendlySessionAttendanceConfirmation();
              created.setSessionId(sessionId);
              created.setTenantId(tenantId);
              created.setConfirmedAt(now);
              return created;
            });
    confirmation.setSessionDate(resolveSessionDate(session));
    confirmation.setConfirmedBy(userId);
    confirmation.setUpdatedAt(now);
    confirmationRepository.save(confirmation);

    return getSessionAttendance(tenantId, sessionId);
  }

  @Transactional
  public void removeSessionAttendance(UUID tenantId, UUID sessionId) {
    requireAttendanceEligibleSession(tenantId, sessionId);
    attendanceRepository.deleteBySessionIdAndTenantId(sessionId, tenantId);
    confirmationRepository.deleteBySessionIdAndTenantId(sessionId, tenantId);
  }

  @Transactional(readOnly = true)
  public AttendanceReportResponse getReport(UUID tenantId, LocalDate from, LocalDate to) {
    if (from == null || to == null) {
      throw new IllegalArgumentException("Informe as datas inicial e final.");
    }
    if (from.isAfter(to)) {
      throw new IllegalArgumentException("A data inicial não pode ser posterior à data final.");
    }

    List<FriendlySessionAttendanceConfirmation> confirmations = confirmationRepository
            .findByTenantIdAndSessionDateBetweenOrderBySessionDateAsc(tenantId, from, to);
    Map<UUID, LocalDate> sessionDates = confirmations.stream()
            .collect(Collectors.toMap(
                    FriendlySessionAttendanceConfirmation::getSessionId,
                    FriendlySessionAttendanceConfirmation::getSessionDate));

    List<FriendlySessionAttendance> attendanceRows = sessionDates.isEmpty()
            ? List.of()
            : attendanceRepository.findByTenantIdAndSessionIdIn(
                    tenantId, sessionDates.keySet());
    Map<UUID, List<LocalDate>> datesByPlayer = new HashMap<>();
    for (FriendlySessionAttendance attendance : attendanceRows) {
      LocalDate sessionDate = sessionDates.get(attendance.getSessionId());
      if (sessionDate != null) {
        datesByPlayer.computeIfAbsent(attendance.getPlayerId(), ignored -> new ArrayList<>())
                .add(sessionDate);
      }
    }

    List<PlayerAttendance> playerRows = playerRepository
            .findAllByTenantIdOrderByNameAsc(tenantId)
            .stream()
            .map(player -> toPlayerAttendance(player, confirmations, datesByPlayer, from))
            .sorted(Comparator.comparingInt(PlayerAttendance::attendances).reversed()
                    .thenComparing(PlayerAttendance::attendanceRate, Comparator.reverseOrder())
                    .thenComparing(PlayerAttendance::playerName, String.CASE_INSENSITIVE_ORDER))
            .toList();

    int totalAttendances = playerRows.stream()
            .mapToInt(PlayerAttendance::attendances)
            .sum();
    int playersWithAttendance = (int) playerRows.stream()
            .filter(player -> player.attendances() > 0)
            .count();
    double averagePlayersPerSession = confirmations.isEmpty()
            ? 0
            : roundOneDecimal((double) totalAttendances / confirmations.size());

    return new AttendanceReportResponse(
            from,
            to,
            confirmations.size(),
            playersWithAttendance,
            totalAttendances,
            averagePlayersPerSession,
            playerRows);
  }

  private PlayerAttendance toPlayerAttendance(
          Player player,
          Collection<FriendlySessionAttendanceConfirmation> confirmations,
          Map<UUID, List<LocalDate>> datesByPlayer,
          LocalDate reportFrom) {
    LocalDate joinedAt = player.getCreatedAt() == null
            ? reportFrom
            : player.getCreatedAt().atZoneSameInstant(GROUP_ZONE).toLocalDate();
    LocalDate eligibleFrom = joinedAt.isAfter(reportFrom) ? joinedAt : reportFrom;
    int eligibleSessions = (int) confirmations.stream()
            .filter(confirmation -> !confirmation.getSessionDate().isBefore(eligibleFrom))
            .count();

    List<LocalDate> attendanceDates = datesByPlayer
            .getOrDefault(player.getId(), List.of())
            .stream()
            .filter(date -> !date.isBefore(eligibleFrom))
            .toList();
    int attendances = attendanceDates.size();
    int absences = Math.max(0, eligibleSessions - attendances);
    double attendanceRate = eligibleSessions == 0
            ? 0
            : roundOneDecimal(attendances * 100.0 / eligibleSessions);
    LocalDate lastAttendance = attendanceDates.stream()
            .max(LocalDate::compareTo)
            .orElse(null);

    return new PlayerAttendance(
            player.getId(),
            player.getName(),
            player.isActive(),
            attendances,
            eligibleSessions,
            absences,
            attendanceRate,
            lastAttendance);
  }

  private FriendlySessionAttendance newAttendance(
          UUID tenantId, UUID sessionId, UUID playerId, LocalDateTime createdAt) {
    FriendlySessionAttendance attendance = new FriendlySessionAttendance();
    attendance.setTenantId(tenantId);
    attendance.setSessionId(sessionId);
    attendance.setPlayerId(playerId);
    attendance.setCreatedAt(createdAt);
    return attendance;
  }

  private Set<UUID> generatedPlayerIds(UUID sessionId) {
    List<UUID> teamIds = generatedTeamRepository
            .findAllBySessionIdOrderByTeamIndexAsc(sessionId)
            .stream()
            .map(GeneratedTeam::getId)
            .toList();
    if (teamIds.isEmpty()) return Set.of();
    return generatedTeamPlayerRepository.findByTeamIdIn(teamIds).stream()
            .map(GeneratedTeamPlayer::getPlayerId)
            .collect(Collectors.toCollection(HashSet::new));
  }

  private TeamGenerationSession requireAttendanceEligibleSession(
          UUID tenantId, UUID sessionId) {
    TeamGenerationSession session = sessionRepository.findById(sessionId)
            .filter(item -> item.getTenantId().equals(tenantId))
            .orElseThrow(() -> new IllegalArgumentException(
                    "Geração de times não encontrada neste grupo."));
    if (championshipRepository.existsByGenerationSession_Id(sessionId)) {
      throw new IllegalArgumentException(
              "A frequência não pode ser confirmada em uma geração usada por campeonato.");
    }
    try {
      JsonNode rules = mapper.readTree(session.getRulesJson());
      String mode = rules.path("mode").asText();
      if (!mode.isBlank() && !"avulso".equals(mode)) {
        throw new IllegalArgumentException(
                "A frequência só pode ser confirmada em sessões amistosas.");
      }
    } catch (IllegalArgumentException exception) {
      throw exception;
    } catch (Exception exception) {
      throw new IllegalArgumentException("Não foi possível validar a sessão amistosa.");
    }
    return session;
  }

  private LocalDate resolveSessionDate(TeamGenerationSession session) {
    if (session.getPlayDate() != null) {
      return session.getPlayDate().atZoneSameInstant(GROUP_ZONE).toLocalDate();
    }
    return session.getCreatedAt().atZoneSameInstant(GROUP_ZONE).toLocalDate();
  }

  private double roundOneDecimal(double value) {
    return Math.round(value * 10.0) / 10.0;
  }
}
