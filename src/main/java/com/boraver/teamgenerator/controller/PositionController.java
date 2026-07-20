package com.boraver.teamgenerator.controller;

import com.boraver.teamgenerator.dto.position.*;
import com.boraver.teamgenerator.service.PositionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/positions")
public class PositionController {
  private final PositionService service;

  public PositionController(PositionService service) {
    this.service = service;
  }

  @PostMapping
  public PositionResponse create(@Valid @RequestBody CreatePositionRequest req) {
    return service.create(req);
  }

  @GetMapping
  public List<PositionResponse> listActive() {
    return service.listActive();
  }

  @GetMapping("/{id}")
  public PositionResponse get(@PathVariable UUID id) {
    return service.get(id);
  }

  @PutMapping("/{id}")
  public PositionResponse update(@PathVariable UUID id, @RequestBody UpdatePositionRequest req) {
    return service.update(id, req);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }
}