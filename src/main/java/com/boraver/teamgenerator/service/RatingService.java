package com.boraver.teamgenerator.service;

import com.boraver.teamgenerator.common.TenantContext;
import com.boraver.teamgenerator.dto.player.PlayerEvolutionDTO;
import com.boraver.teamgenerator.dto.rating.*;
import com.boraver.teamgenerator.entity.PlayerSkillRating;
import com.boraver.teamgenerator.entity.Skill;
import com.boraver.teamgenerator.repository.PlayerRepository;
import com.boraver.teamgenerator.repository.RatingRepository;
import com.boraver.teamgenerator.repository.SkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RatingService {
  private final RatingRepository ratingRepository;
  private final PlayerRepository playerRepository;
  private final SkillRepository skillRepository;

  public RatingService(RatingRepository ratingRepo, PlayerRepository playerRepo, SkillRepository skillRepo) {
    this.ratingRepository = ratingRepo;
    this.playerRepository = playerRepo;
    this.skillRepository = skillRepo;
  }

  private UUID tenantId() {
    return UUID.fromString(Objects.requireNonNull(TenantContext.getTenantId(), "tenant missing"));
  }

  @Transactional
  public void upsert(UpsertRatingsRequest req) {
    UUID tenant = tenantId();

    playerRepository.findByIdAndTenantId(req.playerId(), tenant)
        .orElseThrow(() -> new IllegalArgumentException("Player not found"));

    for (var sr : req.ratings()) {
      skillRepository.findByIdAndTenantId(sr.skillId(), tenant)
          .orElseThrow(() -> new IllegalArgumentException("Skill not found: " + sr.skillId()));

      short newRating = (short) sr.rating();

      var currentOpt = ratingRepository.findCurrent(tenant, req.playerId(), sr.skillId());
      if (currentOpt.isPresent() && currentOpt.get().getRating() == newRating) {
        continue;
      }

      ratingRepository.closeCurrent(tenant, req.playerId(), sr.skillId());

      PlayerSkillRating r = new PlayerSkillRating();
      r.setTenantId(tenant);
      r.setPlayerId(req.playerId());
      r.setSkillId(sr.skillId());
      r.setRating(newRating);
      ratingRepository.save(r);
    }
  }

  public List<PlayerSkillRating> listAllPlayerRatings(UUID playerId) {
    UUID tenant = tenantId();
    playerRepository.findByIdAndTenantId(playerId, tenant)
        .orElseThrow(() -> new IllegalArgumentException("Player not found"));
    return ratingRepository.findAllByPlayer(tenant, playerId);
  }

  public Map<UUID, Integer> currentRatingsMap(UUID playerId) {
    UUID tenant = tenantId();
    var all = ratingRepository.findAllByPlayer(tenant, playerId);
    Map<UUID, Integer> current = new HashMap<>();
    for (var r : all) {
      if (r.getValidTo() == null) current.put(r.getSkillId(), (int) r.getRating());
    }
    return current;
  }

  public List<PlayerEvolutionDTO> getEvolution(UUID playerId) {
    UUID tenant = tenantId();
    List<PlayerSkillRating> ratings = ratingRepository.findAllByPlayerOrderByValidFromAsc(tenant, playerId);
    Map<UUID, String> skillNames = skillRepository.findAllByTenantIdAndActiveTrue(tenant)
            .stream().collect(Collectors.toMap(Skill::getId, Skill::getName));

    Map<String, List<PlayerEvolutionDTO.EvolutionPoint>> grouped = new LinkedHashMap<>();
    for (PlayerSkillRating r : ratings) {
      String skillName = skillNames.get(r.getSkillId());
      if (skillName != null) {
        grouped.computeIfAbsent(skillName, k -> new ArrayList<>())
                .add(new PlayerEvolutionDTO.EvolutionPoint(r.getValidFrom().toLocalDateTime(), (int) r.getRating()));
      }
    }
    return grouped.entrySet().stream()
            .map(e -> new PlayerEvolutionDTO(e.getKey(), e.getValue()))
            .collect(Collectors.toList());
  }
}
