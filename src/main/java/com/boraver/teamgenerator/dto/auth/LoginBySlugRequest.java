package com.boraver.teamgenerator.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginBySlugRequest(
    @NotBlank String tenantSlug,
    @Email @NotBlank String email,
    @NotBlank String password
) {}
