package com.boraver.teamgenerator.dto.subscription;

import java.util.List;

public record SubscriptionStatusDTO(boolean active, String planName, String message, List<String> features) {}