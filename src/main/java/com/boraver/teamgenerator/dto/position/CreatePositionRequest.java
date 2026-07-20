package com.boraver.teamgenerator.dto.position;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePositionRequest(
        @NotBlank @Size(max = 120) String name
) {}