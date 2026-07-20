package com.boraver.teamgenerator.dto.rating;

import java.util.UUID;

public record PlayerRatingResponse(
    UUID skillId,
    int rating,
    boolean current
) {}
