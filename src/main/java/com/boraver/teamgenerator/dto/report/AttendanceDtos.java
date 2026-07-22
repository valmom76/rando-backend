package com.boraver.teamgenerator.dto.report;

import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public final class AttendanceDtos {
  private AttendanceDtos() {}

  public record PlayerOption(
          UUID playerId,
          String playerName,
          boolean active,
          boolean suggestedByGeneration,
          boolean attended
  ) {}

  public record SessionAttendanceResponse(
          UUID sessionId,
          LocalDate sessionDate,
          boolean confirmed,
          LocalDateTime confirmedAt,
          LocalDateTime updatedAt,
          List<PlayerOption> players
  ) {}

  public record UpdateSessionAttendanceRequest(
          @NotEmpty(message = "Selecione ao menos um jogador presente")
          List<UUID> playerIds
  ) {}

  public record PlayerAttendance(
          UUID playerId,
          String playerName,
          boolean active,
          int attendances,
          int eligibleSessions,
          int absences,
          double attendanceRate,
          LocalDate lastAttendance
  ) {}

  public record AttendanceReportResponse(
          LocalDate from,
          LocalDate to,
          int confirmedSessions,
          int playersWithAttendance,
          int totalAttendances,
          double averagePlayersPerSession,
          List<PlayerAttendance> players
  ) {}
}
