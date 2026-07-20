package com.boraver.teamgenerator.dto.match;

import java.util.UUID;

public record FriendlySessionSummary(UUID sessionId, String dateFormatted, int teamCount) {
}
