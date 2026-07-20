package com.boraver.teamgenerator.dto.email;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VerifyEmailRequest(
        @NotBlank String token,
        @NotBlank @Size(min = 6) String password
) {}
