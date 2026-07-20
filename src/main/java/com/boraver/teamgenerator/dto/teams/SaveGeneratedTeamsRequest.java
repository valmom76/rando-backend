package com.boraver.teamgenerator.dto.teams;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class SaveGeneratedTeamsRequest {
  private List<TeamDTO> teams;
}

