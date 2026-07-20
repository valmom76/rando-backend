package com.boraver.teamgenerator.repository;

import com.boraver.teamgenerator.entity.FriendlyMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface FriendlyMatchRepository extends JpaRepository<FriendlyMatch, UUID> {
  List<FriendlyMatch> findAllBySessionIdOrderByCreatedAtAsc(UUID sessionId);
  List<FriendlyMatch> findBySessionIdOrderByCreatedAt(UUID sessionId);
}