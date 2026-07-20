package com.boraver.teamgenerator.security;

public record AuthPrincipal(
    String userId,
    String tenantId,
    String email,
    String role
) {}
