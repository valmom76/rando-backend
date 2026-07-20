package com.boraver.teamgenerator.service;

import com.boraver.teamgenerator.entity.FriendlyMatch;
import com.boraver.teamgenerator.repository.FriendlyMatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FriendlyMatchService {

  private final FriendlyMatchRepository friendlyMatchRepository;

  @Transactional
  public FriendlyMatch registerResult(UUID sessionId, String courtName,
                                      int homeTeamIndex, int awayTeamIndex,
                                      int homeScore, int awayScore,
                                      boolean walkover, Integer winnerTeamIndex) {
    FriendlyMatch match = new FriendlyMatch();
    match.setSessionId(sessionId);
    match.setCourtName(courtName);
    match.setHomeTeamIndex(homeTeamIndex);
    match.setAwayTeamIndex(awayTeamIndex);
    match.setHomeScore(homeScore);
    match.setAwayScore(awayScore);
    match.setWalkover(walkover);
    match.setWinnerTeamIndex(winnerTeamIndex);
    match.setPlayed(true);
    match.setStatus("finished");
    return friendlyMatchRepository.save(match);
  }

  public List<FriendlyMatch> getMatchesBySession(UUID sessionId) {
    return friendlyMatchRepository.findAllBySessionIdOrderByCreatedAtAsc(sessionId);
  }
}