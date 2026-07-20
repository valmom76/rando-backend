package com.boraver.teamgenerator.dto.position;

import java.util.UUID;

public record PositionResponse(
        UUID id,
        String name,
        boolean active
) {}