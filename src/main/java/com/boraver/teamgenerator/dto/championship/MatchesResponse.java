package com.boraver.teamgenerator.dto.championship;

import java.util.List;

public record MatchesResponse(
    List<ChampionshipDetailsResponse.MatchDetails> matches
) {}