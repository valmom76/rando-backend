package com.boraver.teamgenerator.dto.teams;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record MovePlayerRequest(
        @NotNull UUID playerId,
        int fromTeamIndex,
        int toTeamIndex
) {}