package com.boraver.teamgenerator.service;

import com.boraver.teamgenerator.common.TenantContext;
import com.boraver.teamgenerator.dto.position.*;
import com.boraver.teamgenerator.entity.Position;
import com.boraver.teamgenerator.repository.PositionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PositionService {
  private final PositionRepository repository;

  public PositionService(PositionRepository repository) {
    this.repository = repository;
  }

  private UUID tenantId() {
    return UUID.fromString(Objects.requireNonNull(TenantContext.getTenantId(), "tenant missing"));
  }

  @Transactional
  public PositionResponse create(CreatePositionRequest req) {
    UUID tenant = tenantId();
    String name = req.name().trim();
    if (repository.findByTenantIdAndNameIgnoreCase(tenant, name).isPresent()) {
      throw new IllegalArgumentException("Já existe uma posição com o nome '" + name + "'");
    }
    Position p = new Position();
    p.setTenantId(tenant);
    p.setName(name);
    p = repository.save(p);
    return toResponse(p);
  }

  public List<PositionResponse> listActive() {
    return repository.findAllByTenantIdAndActiveTrue(tenantId())
            .stream().map(this::toResponse).collect(Collectors.toList());
  }

  public PositionResponse get(UUID id) {
    Position p = repository.findByIdAndTenantId(id, tenantId())
            .orElseThrow(() -> new IllegalArgumentException("Posição não encontrada"));
    return toResponse(p);
  }

  @Transactional
  public PositionResponse update(UUID id, UpdatePositionRequest req) {
    Position p = repository.findByIdAndTenantId(id, tenantId())
            .orElseThrow(() -> new IllegalArgumentException("Posição não encontrada"));
    if (req.name() != null) p.setName(req.name().trim());
    if (req.active() != null) p.setActive(req.active());
    return toResponse(repository.save(p));
  }

  @Transactional
  public void delete(UUID id) {
    repository.softDelete(id);
  }

  public long countActive(UUID tenantId) {
    return repository.countByTenantIdAndActiveTrue(tenantId);
  }

  private PositionResponse toResponse(Position p) {
    return new PositionResponse(p.getId(), p.getName(), p.isActive());
  }
}