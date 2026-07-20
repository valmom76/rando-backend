package com.boraver.teamgenerator.repository;

import com.boraver.teamgenerator.entity.*;
import org.springframework.data.jpa.repository.*;

import java.util.*;

public interface TenantRepository extends JpaRepository<Tenant, UUID> {
  Optional<Tenant> findBySlug(String slug);
  boolean existsBySlug(String slug);
}
