package com.boraver.teamgenerator.service;

import com.boraver.teamgenerator.dto.tenant.TenantSettingsDTO;
import com.boraver.teamgenerator.dto.tenant.UpdateTenantSettingsRequest;
import com.boraver.teamgenerator.entity.Tenant;
import com.boraver.teamgenerator.repository.TenantRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TenantService {
  private static final long MAX_LOGO_SIZE = 5L * 1024 * 1024;
  private static final byte[] PNG_SIGNATURE = new byte[] {
          (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
  };

  private final TenantRepository tenantRepository;

  @Value("${app.upload-dir:/app/uploads}")
  private String uploadDir;

  public TenantSettingsDTO getSettings(UUID tenantId) {
    Tenant t = tenantRepository.findById(tenantId).orElseThrow();
    return new TenantSettingsDTO(
            t.getName(),
            t.getSportType(),
            t.getLogoUrl(),
            t.getPrimaryColor(),
            t.getSecondaryColor()
    );
  }

  @Transactional
  public TenantSettingsDTO updateSettings(UUID tenantId, UpdateTenantSettingsRequest req) {
    Tenant t = tenantRepository.findById(tenantId).orElseThrow();
    if (req.primaryColor() != null) t.setPrimaryColor(req.primaryColor());
    if (req.secondaryColor() != null) t.setSecondaryColor(req.secondaryColor());
    tenantRepository.save(t);
    return getSettings(tenantId);
  }

  @Transactional
  public String updateLogo(UUID tenantId, MultipartFile file) throws IOException {
    validatePng(file);

    byte[] content = file.getBytes();
    String fileName = "tenant-" + tenantId + "-" + System.currentTimeMillis() + ".png";
    Path logoDirectory = Paths.get(uploadDir).toAbsolutePath().normalize().resolve("logos");
    Files.createDirectories(logoDirectory);

    Path target = logoDirectory.resolve(fileName).normalize();
    if (!target.startsWith(logoDirectory)) {
      throw new IllegalArgumentException("Nome de arquivo inválido");
    }
    Files.write(target, content);

    Tenant t = tenantRepository.findById(tenantId).orElseThrow();
    deletePreviousLogo(t.getLogoUrl(), logoDirectory);

    String logoUrl = "/uploads/logos/" + fileName;
    t.setLogoUrl(logoUrl);
    tenantRepository.save(t);
    return logoUrl;
  }

  private void validatePng(MultipartFile file) throws IOException {
    if (file == null || file.isEmpty()) {
      throw new IllegalArgumentException("Selecione uma imagem PNG");
    }
    if (file.getSize() > MAX_LOGO_SIZE) {
      throw new IllegalArgumentException("A imagem deve ter no máximo 5 MB");
    }

    String originalName = file.getOriginalFilename();
    if (originalName == null || !originalName.toLowerCase(Locale.ROOT).endsWith(".png")) {
      throw new IllegalArgumentException("Apenas imagens PNG são permitidas");
    }

    byte[] header;
    try (InputStream input = file.getInputStream()) {
      header = input.readNBytes(PNG_SIGNATURE.length);
    }
    if (header.length != PNG_SIGNATURE.length) {
      throw new IllegalArgumentException("Arquivo PNG inválido");
    }
    for (int i = 0; i < PNG_SIGNATURE.length; i++) {
      if (header[i] != PNG_SIGNATURE[i]) {
        throw new IllegalArgumentException("Arquivo PNG inválido");
      }
    }
  }

  private void deletePreviousLogo(String currentLogoUrl, Path logoDirectory) {
    if (currentLogoUrl == null || !currentLogoUrl.startsWith("/uploads/logos/")) {
      return;
    }

    String previousFileName = currentLogoUrl.substring(currentLogoUrl.lastIndexOf('/') + 1);
    Path previousFile = logoDirectory.resolve(previousFileName).normalize();
    if (!previousFile.startsWith(logoDirectory)) {
      return;
    }

    try {
      Files.deleteIfExists(previousFile);
    } catch (IOException ignored) {
      // A imagem nova não deve ser descartada caso a limpeza da anterior falhe.
    }
  }
}
