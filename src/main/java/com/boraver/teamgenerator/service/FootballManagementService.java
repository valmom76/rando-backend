package com.boraver.teamgenerator.service;

import com.boraver.teamgenerator.dto.championship.FootballManagementDtos.*;
import com.boraver.teamgenerator.entity.*;
import com.boraver.teamgenerator.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FootballManagementService {
  private static final Set<String> CARD_TYPES = Set.of(
          "YELLOW", "SECOND_YELLOW", "DIRECT_RED");

  private final ChampionshipRepository championshipRepository;
  private final ChampionshipMatchRepository matchRepository;
  private final ChampionshipTeamRepository championshipTeamRepository;
  private final GeneratedTeamRepository generatedTeamRepository;
  private final GeneratedTeamPlayerRepository generatedTeamPlayerRepository;
  private final PlayerRepository playerRepository;
  private final FootballRefereeRepository refereeRepository;
  private final MatchOfficialAssignmentRepository officialRepository;
  private final MatchRosterEntryRepository rosterRepository;
  private final MatchSubstitutionRepository substitutionRepository;
  private final MatchCardRepository cardRepository;
  private final MatchGoalEventRepository goalRepository;
  private final PlayerSuspensionRepository suspensionRepository;
  private final DisciplinaryAppealRepository appealRepository;

  public List<RefereeResponse> listReferees(UUID tenantId) {
    return refereeRepository.findByTenantIdAndActiveTrueOrderByNameAsc(tenantId).stream()
            .map(referee -> new RefereeResponse(referee.getId(), referee.getName()))
            .toList();
  }

  @Transactional
  public RefereeResponse createReferee(UUID tenantId, CreateRefereeRequest request) {
    FootballReferee referee = new FootballReferee();
    referee.setTenantId(tenantId);
    referee.setName(request.name().trim());
    referee = refereeRepository.save(referee);
    return new RefereeResponse(referee.getId(), referee.getName());
  }

  @Transactional
  public void deactivateReferee(UUID tenantId, UUID refereeId) {
    FootballReferee referee = refereeRepository.findByIdAndTenantIdAndActiveTrue(refereeId, tenantId)
            .orElseThrow(() -> new IllegalArgumentException("Árbitro não encontrado"));
    referee.setActive(false);
    refereeRepository.save(referee);
  }

  @Transactional
  public void assignOfficials(
          UUID tenantId, UUID championshipId, UUID matchId,
          AssignOfficialsRequest request) {
    MatchContext context = loadContext(tenantId, championshipId, matchId, true);
    List<UUID> refereeIds = new ArrayList<>();
    refereeIds.add(request.mainRefereeId());
    if (request.assistantOneId() != null) refereeIds.add(request.assistantOneId());
    if (request.assistantTwoId() != null) refereeIds.add(request.assistantTwoId());
    if (new HashSet<>(refereeIds).size() != refereeIds.size()) {
      throw new IllegalArgumentException("O mesmo árbitro não pode ocupar mais de uma função.");
    }
    refereeIds.forEach(id -> refereeRepository.findByIdAndTenantIdAndActiveTrue(id, tenantId)
            .orElseThrow(() -> new IllegalArgumentException("Árbitro inválido para este grupo")));

    officialRepository.deleteByMatchId(matchId);
    List<MatchOfficialAssignment> assignments = new ArrayList<>();
    assignments.add(newOfficial(context, request.mainRefereeId(), "MAIN"));
    if (request.assistantOneId() != null) {
      assignments.add(newOfficial(context, request.assistantOneId(), "ASSISTANT_1"));
    }
    if (request.assistantTwoId() != null) {
      assignments.add(newOfficial(context, request.assistantTwoId(), "ASSISTANT_2"));
    }
    officialRepository.saveAll(assignments);
  }

  @Transactional
  public void saveLineup(
          UUID tenantId, UUID championshipId, UUID matchId,
          SaveLineupRequest request) {
    MatchContext context = loadContext(tenantId, championshipId, matchId, true);
    if (request.home().teamIndex() != context.match().getHomeTeamIndex()
            || request.away().teamIndex() != context.match().getAwayTeamIndex()) {
      throw new IllegalArgumentException("Os times da escalação não correspondem à partida.");
    }
    if (!substitutionRepository.findByMatchIdOrderByCreatedAtAsc(matchId).isEmpty()
            || !cardRepository.findByMatchIdOrderByCreatedAtAsc(matchId).isEmpty()
            || !goalRepository.findByMatchIdOrderByCreatedAtAsc(matchId).isEmpty()) {
      throw new IllegalStateException(
              "A escalação não pode ser alterada depois do primeiro evento da partida.");
    }

    List<MatchRosterEntry> entries = new ArrayList<>();
    entries.addAll(validateAndBuildLineup(context, request.home()));
    entries.addAll(validateAndBuildLineup(context, request.away()));
    rosterRepository.deleteByMatchId(matchId);
    rosterRepository.saveAll(entries);
  }

  @Transactional
  public SubstitutionResponse registerSubstitution(
          UUID tenantId, UUID championshipId, UUID matchId,
          SubstitutionRequest request) {
    MatchContext context = loadContext(tenantId, championshipId, matchId, true);
    validateMatchTeam(context.match(), request.teamIndex());
    if (request.playerOutId().equals(request.playerInId())) {
      throw new IllegalArgumentException("Jogadores de entrada e saída devem ser diferentes.");
    }

    Map<UUID, MatchRosterEntry> roster = rosterRepository.findByMatchId(matchId).stream()
            .filter(entry -> entry.getTeamIndex() == request.teamIndex())
            .collect(Collectors.toMap(MatchRosterEntry::getPlayerId, Function.identity()));
    if (!roster.containsKey(request.playerOutId()) || !roster.containsKey(request.playerInId())) {
      throw new IllegalArgumentException("Os dois jogadores precisam constar na escalação da partida.");
    }

    Set<UUID> onField = currentOnField(matchId, request.teamIndex());
    List<MatchSubstitution> previous = substitutionRepository.findByMatchIdOrderByCreatedAtAsc(matchId);
    boolean alreadyEntered = previous.stream()
            .anyMatch(substitution -> substitution.getPlayerInId().equals(request.playerInId()));
    boolean incomingDismissed = cardRepository.findByMatchIdOrderByCreatedAtAsc(matchId).stream()
            .anyMatch(card -> card.getPlayerId().equals(request.playerInId())
                    && Set.of("SECOND_YELLOW", "DIRECT_RED").contains(card.getCardType()));
    if (!onField.contains(request.playerOutId())) {
      throw new IllegalArgumentException("O jogador que sairá não está em campo.");
    }
    if (!"RESERVE".equals(roster.get(request.playerInId()).getRosterRole())
            || alreadyEntered || incomingDismissed) {
      throw new IllegalArgumentException("O jogador que entrará deve ser um reserva que ainda não participou.");
    }

    MatchSubstitution substitution = new MatchSubstitution();
    substitution.setChampionshipId(championshipId);
    substitution.setMatchId(matchId);
    substitution.setTeamIndex(request.teamIndex());
    substitution.setPlayerOutId(request.playerOutId());
    substitution.setPlayerInId(request.playerInId());
    substitution.setMatchMinute(request.minute());
    substitution = substitutionRepository.save(substitution);

    Map<UUID, Player> players = loadPlayers(Set.of(request.playerOutId(), request.playerInId()));
    return toSubstitutionResponse(substitution, players);
  }

  @Transactional
  public CardResponse registerCard(
          UUID tenantId, UUID championshipId, UUID matchId,
          CardRequest request) {
    MatchContext context = loadContext(tenantId, championshipId, matchId, true);
    validateMatchTeam(context.match(), request.teamIndex());
    String cardType = request.cardType().trim().toUpperCase(Locale.ROOT);
    if (!CARD_TYPES.contains(cardType)) {
      throw new IllegalArgumentException("Tipo de cartão inválido.");
    }
    MatchRosterEntry rosterEntry = rosterRepository
            .findByMatchIdAndPlayerId(matchId, request.playerId())
            .orElseThrow(() -> new IllegalArgumentException(
                    "O jogador precisa constar na escalação para receber cartão."));
    if (rosterEntry.getTeamIndex() != request.teamIndex()) {
      throw new IllegalArgumentException("Jogador não pertence ao time informado.");
    }

    MatchCard card = new MatchCard();
    card.setChampionshipId(championshipId);
    card.setMatchId(matchId);
    card.setTeamIndex(request.teamIndex());
    card.setPlayerId(request.playerId());
    card.setCardType(cardType);
    card.setMatchMinute(request.minute());
    card.setReason(normalizeNullable(request.reason()));
    card = cardRepository.save(card);
    createAutomaticSuspension(context.championship(), card);

    Player player = playerRepository.findById(request.playerId())
            .orElseThrow(() -> new IllegalArgumentException("Jogador não encontrado"));
    return toCardResponse(card, player.getName());
  }

  @Transactional
  public GoalResponse registerGoal(
          UUID tenantId, UUID championshipId, UUID matchId,
          GoalRequest request) {
    MatchContext context = loadContext(tenantId, championshipId, matchId, true);
    validateMatchTeam(context.match(), request.scoringTeamIndex());

    int scorerTeamIndex = request.ownGoal()
            ? opponentTeamIndex(context.match(), request.scoringTeamIndex())
            : request.scoringTeamIndex();
    if (!currentOnField(matchId, scorerTeamIndex).contains(request.playerId())) {
      throw new IllegalArgumentException(
              "O autor selecionado não está em campo pelo time correspondente.");
    }

    MatchGoalEvent goal = new MatchGoalEvent();
    goal.setChampionshipId(championshipId);
    goal.setMatchId(matchId);
    goal.setScoringTeamIndex(request.scoringTeamIndex());
    goal.setScorerTeamIndex(scorerTeamIndex);
    goal.setPlayerId(request.playerId());
    goal.setOwnGoal(request.ownGoal());
    goal.setMatchMinute(request.minute());
    goal = goalRepository.save(goal);

    Player player = playerRepository.findById(request.playerId())
            .orElseThrow(() -> new IllegalArgumentException("Jogador não encontrado"));
    return toGoalResponse(goal, player.getName());
  }

  @Transactional
  public void deleteGoal(
          UUID tenantId, UUID championshipId, UUID matchId, UUID goalId) {
    loadContext(tenantId, championshipId, matchId, true);
    MatchGoalEvent goal = goalRepository.findById(goalId)
            .filter(item -> item.getChampionshipId().equals(championshipId)
                    && item.getMatchId().equals(matchId))
            .orElseThrow(() -> new IllegalArgumentException("Gol não encontrado"));
    goalRepository.delete(goal);
  }

  public MatchManagementResponse getMatchManagement(
          UUID tenantId, UUID championshipId, UUID matchId) {
    MatchContext context = loadContext(tenantId, championshipId, matchId, false);
    List<MatchRosterEntry> roster = rosterRepository.findByMatchId(matchId);
    Map<UUID, String> rosterRoles = roster.stream()
            .collect(Collectors.toMap(MatchRosterEntry::getPlayerId, MatchRosterEntry::getRosterRole));

    Map<Integer, List<Player>> playersByTeam = loadMatchPlayers(context);
    Set<UUID> allPlayerIds = playersByTeam.values().stream()
            .flatMap(List::stream).map(Player::getId).collect(Collectors.toSet());
    Map<UUID, Integer> suspensions = activeSuspensionTotals(
            championshipId, allPlayerIds, matchId);
    List<MatchPlayerResponse> players = playersByTeam.entrySet().stream()
            .flatMap(entry -> entry.getValue().stream().map(player -> new MatchPlayerResponse(
                    player.getId(), player.getName(), entry.getKey(),
                    rosterRoles.get(player.getId()), suspensions.containsKey(player.getId()),
                    suspensions.get(player.getId()))))
            .sorted(Comparator.comparingInt(MatchPlayerResponse::teamIndex)
                    .thenComparing(MatchPlayerResponse::playerName))
            .toList();

    List<MatchOfficialAssignment> assignments = officialRepository.findByMatchId(matchId);
    Map<UUID, FootballReferee> referees = refereeRepository.findAllById(
                    assignments.stream().map(MatchOfficialAssignment::getRefereeId).toList())
            .stream().collect(Collectors.toMap(FootballReferee::getId, Function.identity()));
    List<OfficialResponse> officials = assignments.stream()
            .map(assignment -> new OfficialResponse(
                    assignment.getOfficialRole(), assignment.getRefereeId(),
                    Optional.ofNullable(referees.get(assignment.getRefereeId()))
                            .map(FootballReferee::getName).orElse("Árbitro inativo")))
            .toList();

    Map<UUID, Player> playerMap = loadPlayers(allPlayerIds);
    List<CardResponse> cards = cardRepository.findByMatchIdOrderByCreatedAtAsc(matchId).stream()
            .map(card -> toCardResponse(card,
                    Optional.ofNullable(playerMap.get(card.getPlayerId()))
                            .map(Player::getName).orElse("Jogador")))
            .toList();
    List<SubstitutionResponse> substitutions = substitutionRepository
            .findByMatchIdOrderByCreatedAtAsc(matchId).stream()
            .map(substitution -> toSubstitutionResponse(substitution, playerMap))
            .toList();
    List<GoalResponse> goals = goalRepository.findByMatchIdOrderByCreatedAtAsc(matchId).stream()
            .map(goal -> toGoalResponse(goal,
                    Optional.ofNullable(playerMap.get(goal.getPlayerId()))
                            .map(Player::getName).orElse("Jogador")))
            .toList();

    Championship championship = context.championship();
    return new MatchManagementResponse(
            championship.getStartersPerTeam(), championship.getYellowCardsForSuspension(),
            championship.getRedCardSuspensionMatches(), players, officials, cards,
            substitutions, goals);
  }

  public List<ScorerStandingResponse> getScorers(UUID tenantId, UUID championshipId) {
    loadChampionship(tenantId, championshipId);
    List<MatchGoalEvent> events = goalRepository
            .findByChampionshipIdOrderByCreatedAtAsc(championshipId);
    Set<UUID> playerIds = events.stream()
            .map(MatchGoalEvent::getPlayerId).collect(Collectors.toSet());
    Map<UUID, Player> players = loadPlayers(playerIds);
    Map<Integer, String> teamNames = championshipTeamRepository.findByChampionshipId(championshipId).stream()
            .collect(Collectors.toMap(
                    ChampionshipTeam::getTeamIndex,
                    team -> Optional.ofNullable(team.getName())
                            .orElse("Time " + team.getTeamIndex()),
                    (first, ignored) -> first));

    return events.stream()
            .collect(Collectors.groupingBy(goal ->
                    new ScorerKey(goal.getPlayerId(), goal.getScorerTeamIndex())))
            .entrySet().stream()
            .map(entry -> {
              ScorerKey key = entry.getKey();
              String playerName = Optional.ofNullable(players.get(key.playerId()))
                      .map(Player::getName).orElse("Jogador");
              long goals = entry.getValue().stream().filter(goal -> !goal.isOwnGoal()).count();
              long ownGoals = entry.getValue().stream().filter(MatchGoalEvent::isOwnGoal).count();
              return new ScorerStandingResponse(
                      key.playerId(), playerName, key.teamIndex(),
                      teamNames.getOrDefault(key.teamIndex(), "Time " + key.teamIndex()),
                      goals, ownGoals);
            })
            .sorted(Comparator.comparingLong(ScorerStandingResponse::goals).reversed()
                    .thenComparing(ScorerStandingResponse::playerName))
            .toList();
  }

  public DisciplineResponse getDiscipline(UUID tenantId, UUID championshipId) {
    Championship championship = loadChampionship(tenantId, championshipId);
    List<PlayerSuspension> suspensions = suspensionRepository
            .findByChampionshipIdOrderByCreatedAtDesc(championshipId);
    Set<UUID> playerIds = suspensions.stream().map(PlayerSuspension::getPlayerId).collect(Collectors.toSet());
    Map<UUID, Player> players = loadPlayers(playerIds);
    Map<Integer, String> teamNames = championshipTeamRepository.findByChampionshipId(championshipId).stream()
            .collect(Collectors.toMap(
                    ChampionshipTeam::getTeamIndex,
                    team -> Optional.ofNullable(team.getName()).orElse("Time " + team.getTeamIndex()),
                    (first, ignored) -> first));

    List<SuspensionResponse> responses = suspensions.stream().map(suspension -> {
      Optional<DisciplinaryAppeal> appeal = appealRepository
              .findFirstBySuspensionIdOrderByCreatedAtDesc(suspension.getId());
      return new SuspensionResponse(
              suspension.getId(), suspension.getPlayerId(),
              Optional.ofNullable(players.get(suspension.getPlayerId())).map(Player::getName).orElse("Jogador"),
              suspension.getTeamIndex(),
              teamNames.getOrDefault(suspension.getTeamIndex(), "Time " + suspension.getTeamIndex()),
              suspension.getSuspensionReason(), suspension.getTotalMatches(),
              suspension.getRemainingMatches(), suspension.getStatus(),
              suspension.getSourceMatchId(), appeal.map(DisciplinaryAppeal::getId).orElse(null),
              appeal.map(DisciplinaryAppeal::getStatus).orElse(null),
              appeal.map(DisciplinaryAppeal::getReason).orElse(null),
              appeal.map(DisciplinaryAppeal::getDecisionNotes).orElse(null),
              appeal.map(DisciplinaryAppeal::getDecidedBy).orElse(null),
              appeal.map(DisciplinaryAppeal::getDecidedAt).orElse(null));
    }).toList();
    return new DisciplineResponse(responses);
  }

  @Transactional
  public void createAppeal(
          UUID tenantId, UUID championshipId, UUID suspensionId,
          AppealRequest request) {
    loadChampionship(tenantId, championshipId);
    PlayerSuspension suspension = suspensionRepository.findById(suspensionId)
            .filter(item -> item.getChampionshipId().equals(championshipId))
            .orElseThrow(() -> new IllegalArgumentException("Suspensão não encontrada"));
    if (!"ACTIVE".equals(suspension.getStatus())) {
      throw new IllegalStateException("Somente suspensões ativas podem receber recurso.");
    }
    if (appealRepository.findFirstBySuspensionIdAndStatusOrderByCreatedAtDesc(
            suspensionId, "PENDING").isPresent()) {
      throw new IllegalStateException("Já existe um recurso pendente para esta suspensão.");
    }

    DisciplinaryAppeal appeal = new DisciplinaryAppeal();
    appeal.setChampionshipId(championshipId);
    appeal.setSuspensionId(suspensionId);
    appeal.setReason(request.reason().trim());
    appealRepository.save(appeal);
  }

  @Transactional
  public void decideAppeal(
          UUID tenantId, UUID championshipId, UUID appealId,
          String decidedBy, AppealDecisionRequest request) {
    loadChampionship(tenantId, championshipId);
    DisciplinaryAppeal appeal = appealRepository.findById(appealId)
            .filter(item -> item.getChampionshipId().equals(championshipId))
            .orElseThrow(() -> new IllegalArgumentException("Recurso não encontrado"));
    if (!"PENDING".equals(appeal.getStatus())) {
      throw new IllegalStateException("Este recurso já foi julgado.");
    }

    appeal.setStatus(request.accepted() ? "ACCEPTED" : "REJECTED");
    appeal.setDecisionNotes(request.notes().trim());
    appeal.setDecidedBy(decidedBy);
    appeal.setDecidedAt(LocalDateTime.now());
    appealRepository.save(appeal);

    if (request.accepted()) {
      PlayerSuspension suspension = suspensionRepository.findById(appeal.getSuspensionId())
              .orElseThrow(() -> new IllegalArgumentException("Suspensão não encontrada"));
      suspension.setStatus("REVOKED_BY_APPEAL");
      suspension.setRemainingMatches(0);
      suspension.setUpdatedAt(LocalDateTime.now());
      suspensionRepository.save(suspension);
    }
  }

  @Transactional
  public void applySuspensionsAfterMatch(UUID championshipId, ChampionshipMatch match) {
    Set<Integer> participatingTeams = Set.of(match.getHomeTeamIndex(), match.getAwayTeamIndex());
    Map<UUID, List<PlayerSuspension>> byPlayer = suspensionRepository
            .findByChampionshipIdAndStatus(championshipId, "ACTIVE").stream()
            .filter(suspension -> participatingTeams.contains(suspension.getTeamIndex()))
            .filter(suspension -> !suspension.getSourceMatchId().equals(match.getId()))
            .collect(Collectors.groupingBy(PlayerSuspension::getPlayerId));

    for (List<PlayerSuspension> playerSuspensions : byPlayer.values()) {
      PlayerSuspension suspension = playerSuspensions.stream()
              .min(Comparator.comparing(PlayerSuspension::getCreatedAt))
              .orElseThrow();
      suspension.setRemainingMatches(Math.max(0, suspension.getRemainingMatches() - 1));
      suspension.setUpdatedAt(LocalDateTime.now());
      if (suspension.getRemainingMatches() == 0) suspension.setStatus("SERVED");
      suspensionRepository.save(suspension);
    }
  }

  public void validateReadyForResult(Championship championship, ChampionshipMatch match) {
    if (!championship.isFootballManagementEnabled()) return;

    List<MatchRosterEntry> roster = rosterRepository.findByMatchId(match.getId());
    for (int teamIndex : List.of(match.getHomeTeamIndex(), match.getAwayTeamIndex())) {
      long starters = roster.stream()
              .filter(entry -> entry.getTeamIndex() == teamIndex)
              .filter(entry -> "STARTER".equals(entry.getRosterRole()))
              .count();
      if (starters != championship.getStartersPerTeam()) {
        throw new IllegalStateException(
                "Configure titulares e reservas dos dois times antes de encerrar a partida.");
      }
    }
    boolean hasMainReferee = officialRepository.findByMatchId(match.getId()).stream()
            .anyMatch(official -> "MAIN".equals(official.getOfficialRole()));
    if (!hasMainReferee) {
      throw new IllegalStateException(
              "Defina o árbitro principal antes de encerrar a partida.");
    }
  }

  public void validateGoalScore(
          Championship championship, ChampionshipMatch match,
          int homeScore, int awayScore) {
    if (!championship.isFootballManagementEnabled()) return;
    Map<Integer, Long> goalsByTeam = goalRepository
            .findByMatchIdOrderByCreatedAtAsc(match.getId()).stream()
            .collect(Collectors.groupingBy(
                    MatchGoalEvent::getScoringTeamIndex, Collectors.counting()));
    long registeredHome = goalsByTeam.getOrDefault(match.getHomeTeamIndex(), 0L);
    long registeredAway = goalsByTeam.getOrDefault(match.getAwayTeamIndex(), 0L);
    if (registeredHome != homeScore || registeredAway != awayScore) {
      throw new IllegalStateException(
              "O placar deve corresponder aos gols e autores registrados.");
    }
  }

  @Transactional
  public void clearGoalsForWalkover(UUID matchId) {
    goalRepository.deleteByMatchId(matchId);
  }

  @Transactional
  public void deleteChampionshipData(UUID championshipId) {
    appealRepository.deleteByChampionshipId(championshipId);
    suspensionRepository.deleteByChampionshipId(championshipId);
    goalRepository.deleteByChampionshipId(championshipId);
    cardRepository.deleteByChampionshipId(championshipId);
    substitutionRepository.deleteByChampionshipId(championshipId);
    rosterRepository.deleteByChampionshipId(championshipId);
    officialRepository.deleteByChampionshipId(championshipId);
  }

  private List<MatchRosterEntry> validateAndBuildLineup(
          MatchContext context, TeamLineupRequest lineup) {
    validateMatchTeam(context.match(), lineup.teamIndex());
    if (lineup.starters().size() != context.championship().getStartersPerTeam()) {
      throw new IllegalArgumentException(
              "Cada time deve ter exatamente " + context.championship().getStartersPerTeam()
                      + " titulares.");
    }
    Set<UUID> selected = new HashSet<>(lineup.starters());
    if (selected.size() != lineup.starters().size()) {
      throw new IllegalArgumentException("Há titulares duplicados.");
    }
    if (lineup.reserves().stream().anyMatch(selected::contains)) {
      throw new IllegalArgumentException("Um jogador não pode ser titular e reserva ao mesmo tempo.");
    }
    if (new HashSet<>(lineup.reserves()).size() != lineup.reserves().size()) {
      throw new IllegalArgumentException("Há reservas duplicados.");
    }
    selected.addAll(lineup.reserves());

    Set<UUID> teamPlayers = loadTeamPlayerIds(context, lineup.teamIndex());
    Set<UUID> suspended = activeSuspensionTotals(
            context.championship().getId(), teamPlayers,
            context.match().getId()).keySet();
    Set<UUID> eligible = new HashSet<>(teamPlayers);
    eligible.removeAll(suspended);
    if (!eligible.equals(selected)) {
      throw new IllegalArgumentException(
              "Todos os jogadores disponíveis devem ser classificados como titulares ou reservas; suspensos ficam fora.");
    }

    List<MatchRosterEntry> entries = new ArrayList<>();
    lineup.starters().forEach(playerId -> entries.add(newRosterEntry(
            context, lineup.teamIndex(), playerId, "STARTER")));
    lineup.reserves().forEach(playerId -> entries.add(newRosterEntry(
            context, lineup.teamIndex(), playerId, "RESERVE")));
    return entries;
  }

  private void createAutomaticSuspension(Championship championship, MatchCard card) {
    int matches = 0;
    String reason = null;
    if ("DIRECT_RED".equals(card.getCardType())) {
      matches = championship.getRedCardSuspensionMatches();
      reason = "DIRECT_RED";
    } else if ("SECOND_YELLOW".equals(card.getCardType())) {
      matches = 1;
      reason = "SECOND_YELLOW";
    } else if ("YELLOW".equals(card.getCardType())) {
      int yellowCount = cardRepository.findByChampionshipIdAndPlayerIdAndCardType(
              championship.getId(), card.getPlayerId(), "YELLOW").size();
      if (yellowCount % championship.getYellowCardsForSuspension() == 0) {
        matches = 1;
        reason = "YELLOW_ACCUMULATION";
      }
    }
    if (matches == 0) return;

    PlayerSuspension suspension = new PlayerSuspension();
    suspension.setChampionshipId(championship.getId());
    suspension.setPlayerId(card.getPlayerId());
    suspension.setTeamIndex(card.getTeamIndex());
    suspension.setSourceMatchId(card.getMatchId());
    suspension.setSourceCardId(card.getId());
    suspension.setSuspensionReason(reason);
    suspension.setTotalMatches(matches);
    suspension.setRemainingMatches(matches);
    suspensionRepository.save(suspension);
  }

  private MatchContext loadContext(
          UUID tenantId, UUID championshipId, UUID matchId, boolean requireOpenMatch) {
    Championship championship = loadChampionship(tenantId, championshipId);
    ChampionshipMatch match = matchRepository.findById(matchId)
            .filter(item -> item.getChampionshipId().equals(championshipId))
            .orElseThrow(() -> new IllegalArgumentException("Partida não encontrada"));
    if (requireOpenMatch && match.isPlayed()) {
      throw new IllegalStateException("A gestão da partida não pode ser alterada após o encerramento.");
    }
    return new MatchContext(championship, match);
  }

  private Championship loadChampionship(UUID tenantId, UUID championshipId) {
    Championship championship = championshipRepository.findById(championshipId)
            .filter(item -> item.getTenantId().equals(tenantId))
            .orElseThrow(() -> new SecurityException("Campeonato não pertence a este grupo"));
    if (championship.getSportType() != SportType.FOOTBALL) {
      throw new IllegalArgumentException("Gestão de futebol disponível apenas para campeonatos de futebol.");
    }
    return championship;
  }

  private Map<Integer, List<Player>> loadMatchPlayers(MatchContext context) {
    Map<Integer, List<Player>> result = new HashMap<>();
    for (int teamIndex : List.of(
            context.match().getHomeTeamIndex(), context.match().getAwayTeamIndex())) {
      Set<UUID> ids = loadTeamPlayerIds(context, teamIndex);
      result.put(teamIndex, playerRepository.findAllById(ids).stream()
              .sorted(Comparator.comparing(Player::getName)).toList());
    }
    return result;
  }

  private Set<UUID> loadTeamPlayerIds(MatchContext context, int teamIndex) {
    GeneratedTeam team = generatedTeamRepository.findBySessionIdAndTeamIndex(
                    context.championship().getGenerationSession().getId(), teamIndex)
            .orElseThrow(() -> new IllegalArgumentException("Time não encontrado"));
    return generatedTeamPlayerRepository.findByTeamId(team.getId()).stream()
            .map(GeneratedTeamPlayer::getPlayerId).collect(Collectors.toSet());
  }

  private Map<UUID, Integer> activeSuspensionTotals(
          UUID championshipId,
          Collection<UUID> playerIds,
          UUID ignoredSourceMatchId) {
    return suspensionRepository.findByChampionshipIdAndStatus(championshipId, "ACTIVE").stream()
            .filter(suspension -> playerIds.contains(suspension.getPlayerId()))
            .filter(suspension -> ignoredSourceMatchId == null
                    || !ignoredSourceMatchId.equals(suspension.getSourceMatchId()))
            .collect(Collectors.groupingBy(
                    PlayerSuspension::getPlayerId,
                    Collectors.summingInt(PlayerSuspension::getRemainingMatches)));
  }

  private void validateMatchTeam(ChampionshipMatch match, int teamIndex) {
    if (teamIndex != match.getHomeTeamIndex() && teamIndex != match.getAwayTeamIndex()) {
      throw new IllegalArgumentException("Time não pertence à partida.");
    }
  }

  private int opponentTeamIndex(ChampionshipMatch match, int teamIndex) {
    return teamIndex == match.getHomeTeamIndex()
            ? match.getAwayTeamIndex()
            : match.getHomeTeamIndex();
  }

  private Set<UUID> currentOnField(UUID matchId, int teamIndex) {
    Set<UUID> onField = rosterRepository.findByMatchId(matchId).stream()
            .filter(entry -> entry.getTeamIndex() == teamIndex)
            .filter(entry -> "STARTER".equals(entry.getRosterRole()))
            .map(MatchRosterEntry::getPlayerId)
            .collect(Collectors.toSet());
    substitutionRepository.findByMatchIdOrderByCreatedAtAsc(matchId).stream()
            .filter(substitution -> substitution.getTeamIndex() == teamIndex)
            .forEach(substitution -> {
              onField.remove(substitution.getPlayerOutId());
              onField.add(substitution.getPlayerInId());
            });
    cardRepository.findByMatchIdOrderByCreatedAtAsc(matchId).stream()
            .filter(card -> card.getTeamIndex() == teamIndex)
            .filter(card -> Set.of("SECOND_YELLOW", "DIRECT_RED").contains(card.getCardType()))
            .forEach(card -> onField.remove(card.getPlayerId()));
    return onField;
  }

  private MatchOfficialAssignment newOfficial(MatchContext context, UUID refereeId, String role) {
    MatchOfficialAssignment assignment = new MatchOfficialAssignment();
    assignment.setChampionshipId(context.championship().getId());
    assignment.setMatchId(context.match().getId());
    assignment.setRefereeId(refereeId);
    assignment.setOfficialRole(role);
    return assignment;
  }

  private MatchRosterEntry newRosterEntry(
          MatchContext context, int teamIndex, UUID playerId, String role) {
    MatchRosterEntry entry = new MatchRosterEntry();
    entry.setChampionshipId(context.championship().getId());
    entry.setMatchId(context.match().getId());
    entry.setTeamIndex(teamIndex);
    entry.setPlayerId(playerId);
    entry.setRosterRole(role);
    return entry;
  }

  private Map<UUID, Player> loadPlayers(Collection<UUID> ids) {
    if (ids.isEmpty()) return Map.of();
    return playerRepository.findAllById(ids).stream()
            .collect(Collectors.toMap(Player::getId, Function.identity()));
  }

  private CardResponse toCardResponse(MatchCard card, String playerName) {
    return new CardResponse(
            card.getId(), card.getPlayerId(), playerName, card.getTeamIndex(),
            card.getCardType(), card.getMatchMinute(), card.getReason(), card.getCreatedAt());
  }

  private SubstitutionResponse toSubstitutionResponse(
          MatchSubstitution substitution, Map<UUID, Player> players) {
    return new SubstitutionResponse(
            substitution.getId(), substitution.getTeamIndex(),
            substitution.getPlayerOutId(),
            Optional.ofNullable(players.get(substitution.getPlayerOutId()))
                    .map(Player::getName).orElse("Jogador"),
            substitution.getPlayerInId(),
            Optional.ofNullable(players.get(substitution.getPlayerInId()))
                    .map(Player::getName).orElse("Jogador"),
            substitution.getMatchMinute());
  }

  private GoalResponse toGoalResponse(MatchGoalEvent goal, String playerName) {
    return new GoalResponse(
            goal.getId(), goal.getScoringTeamIndex(), goal.getScorerTeamIndex(),
            goal.getPlayerId(), playerName, goal.isOwnGoal(),
            goal.getMatchMinute(), goal.getCreatedAt());
  }

  private String normalizeNullable(String value) {
    if (value == null || value.isBlank()) return null;
    return value.trim();
  }

  private record MatchContext(Championship championship, ChampionshipMatch match) {}
  private record ScorerKey(UUID playerId, int teamIndex) {}
}
