package com.boraver.teamgenerator.dto.tenant;

import jakarta.validation.constraints.Pattern;

public record UpdateTenantSettingsRequest(
        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Cor principal inválida")
        String primaryColor,

        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Cor secundária inválida")
        String secondaryColor
) {}
