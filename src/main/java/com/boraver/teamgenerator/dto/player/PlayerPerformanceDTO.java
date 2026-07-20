package com.boraver.teamgenerator.dto.player;

import java.util.Map;
import java.util.UUID;

public record PlayerPerformanceDTO(
        UUID id,
        String name,
        String sex,
        double overall,
        String nivel,
        String bestSkill,
        String worstSkill,
        Map<String, Integer> skills,
        String lastUpdate
) {}