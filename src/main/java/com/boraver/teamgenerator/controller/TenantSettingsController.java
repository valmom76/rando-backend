package com.boraver.teamgenerator.controller;

import com.boraver.teamgenerator.common.TenantContext;
import com.boraver.teamgenerator.dto.tenant.TenantSettingsDTO;
import com.boraver.teamgenerator.dto.tenant.UpdateTenantSettingsRequest;
import com.boraver.teamgenerator.service.TenantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/tenant/settings")
@RequiredArgsConstructor
public class TenantSettingsController {
  private final TenantService tenantService;

  @GetMapping
  public ResponseEntity<TenantSettingsDTO> getSettings() {
    UUID tenantId = UUID.fromString(TenantContext.getTenantId());
    return ResponseEntity.ok(tenantService.getSettings(tenantId));
  }

  @PutMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<TenantSettingsDTO> updateSettings(@Valid @RequestBody UpdateTenantSettingsRequest req) {
    UUID tenantId = UUID.fromString(TenantContext.getTenantId());
    return ResponseEntity.ok(tenantService.updateSettings(tenantId, req));
  }

  @PostMapping("/logo")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Map<String, String>> uploadLogo(@RequestParam("file") MultipartFile file) throws IOException {
    UUID tenantId = UUID.fromString(TenantContext.getTenantId());
    String logoUrl = tenantService.updateLogo(tenantId, file);
    return ResponseEntity.ok(Map.of("logoUrl", logoUrl));
  }
}
