package com.boraver.teamgenerator.controller;

import com.boraver.teamgenerator.dto.teams.*;
import com.boraver.teamgenerator.service.HistoryService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/history")
public class HistoryController {

  private final HistoryService service;

  public HistoryController(HistoryService service) { this.service = service; }

  @GetMapping("/sessions")
  public List<SessionSummaryResponse> sessions() {
    return service.listLatest();
  }

  @GetMapping("/sessions/{sessionId}")
  public SessionDetailResponse sessionDetail(@PathVariable UUID sessionId) {
    return service.detail(sessionId);
  }
}

