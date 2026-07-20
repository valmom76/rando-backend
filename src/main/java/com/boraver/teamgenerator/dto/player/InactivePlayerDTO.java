package com.boraver.teamgenerator.dto.player;

import java.time.LocalDateTime;

public record InactivePlayerDTO(
        String name,
        LocalDateTime lastParticipation
) {}