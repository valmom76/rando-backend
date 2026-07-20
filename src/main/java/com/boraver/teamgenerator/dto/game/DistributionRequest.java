package com.boraver.teamgenerator.dto.game;

import java.util.List;
import java.util.UUID;

public record DistributionRequest(UUID sessionId, int courtCount, List<String> courtNames) {}
