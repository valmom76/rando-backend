package com.boraver.teamgenerator.dto.tenant;

import com.boraver.teamgenerator.entity.SportType;

public record TenantSettingsDTO(
        String groupName,
        SportType sportType,
        String logoUrl,
        String primaryColor,
        String secondaryColor
) {}
