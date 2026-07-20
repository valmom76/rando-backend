package com.boraver.teamgenerator.dto.game;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record StartSessionRequest(@NotNull UUID teamGenerationId) {}