package com.boraver.teamgenerator.dto.match;

import java.util.List;
import java.util.UUID;

public record TeamStats(
    List<UUID> playerIds,
    long wins,
    long pointDiff
) {}