package com.boraver.teamgenerator.dto.auth;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank String tenantId, // UUID string
    @Email @NotBlank String email,
    @NotBlank String password
) {}
