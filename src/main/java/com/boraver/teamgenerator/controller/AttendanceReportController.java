package com.boraver.teamgenerator.controller;

import com.boraver.teamgenerator.common.TenantContext;
import com.boraver.teamgenerator.dto.report.AttendanceDtos.AttendanceReportResponse;
import com.boraver.teamgenerator.dto.report.AttendanceDtos.SessionAttendanceResponse;
import com.boraver.teamgenerator.dto.report.AttendanceDtos.UpdateSessionAttendanceRequest;
import com.boraver.teamgenerator.service.AttendanceReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/management/attendance")
@RequiredArgsConstructor
public class AttendanceReportController {

  private final AttendanceReportService attendanceReportService;

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<AttendanceReportResponse> report(
          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
    UUID tenantId = UUID.fromString(TenantContext.getTenantId());
    return ResponseEntity.ok(attendanceReportService.getReport(tenantId, from, to));
  }

  @GetMapping("/sessions/{sessionId}")
  public ResponseEntity<SessionAttendanceResponse> sessionAttendance(
          @PathVariable UUID sessionId) {
    UUID tenantId = UUID.fromString(TenantContext.getTenantId());
    return ResponseEntity.ok(
            attendanceReportService.getSessionAttendance(tenantId, sessionId));
  }

  @PutMapping("/sessions/{sessionId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SessionAttendanceResponse> confirmSessionAttendance(
          @PathVariable UUID sessionId,
          @Valid @RequestBody UpdateSessionAttendanceRequest request,
          Authentication authentication) {
    UUID tenantId = UUID.fromString(TenantContext.getTenantId());
    UUID userId = (UUID) authentication.getPrincipal();
    return ResponseEntity.ok(attendanceReportService.confirmSessionAttendance(
            tenantId, userId, sessionId, request));
  }

  @DeleteMapping("/sessions/{sessionId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> removeSessionAttendance(@PathVariable UUID sessionId) {
    UUID tenantId = UUID.fromString(TenantContext.getTenantId());
    attendanceReportService.removeSessionAttendance(tenantId, sessionId);
    return ResponseEntity.noContent().build();
  }
}
