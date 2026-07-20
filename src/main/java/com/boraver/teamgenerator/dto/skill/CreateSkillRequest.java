package com.boraver.teamgenerator.dto.skill;

import jakarta.validation.constraints.NotBlank;

public record CreateSkillRequest(
    @NotBlank String name
) {}