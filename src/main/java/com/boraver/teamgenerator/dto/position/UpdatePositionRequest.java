package com.boraver.teamgenerator.dto.position;

public record UpdatePositionRequest(
        String name,
        Boolean active
) {}