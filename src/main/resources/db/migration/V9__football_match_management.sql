ALTER TABLE championships
    ADD COLUMN starters_per_team INT NOT NULL DEFAULT 11,
    ADD COLUMN yellow_cards_for_suspension INT NOT NULL DEFAULT 3,
    ADD COLUMN red_card_suspension_matches INT NOT NULL DEFAULT 1,
    ADD COLUMN football_management_enabled BOOLEAN NOT NULL DEFAULT FALSE;

CREATE TABLE football_referees (
    id CHAR(36) PRIMARY KEY,
    tenant_id CHAR(36) NOT NULL,
    name VARCHAR(120) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_referee_tenant_active (tenant_id, active)
) ENGINE=InnoDB;

CREATE TABLE match_official_assignments (
    id CHAR(36) PRIMARY KEY,
    championship_id CHAR(36) NOT NULL,
    match_id CHAR(36) NOT NULL,
    referee_id CHAR(36) NOT NULL,
    official_role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_match_official_role UNIQUE (match_id, official_role),
    INDEX idx_match_official_championship (championship_id),
    INDEX idx_match_official_match (match_id)
) ENGINE=InnoDB;

CREATE TABLE match_roster_entries (
    id CHAR(36) PRIMARY KEY,
    championship_id CHAR(36) NOT NULL,
    match_id CHAR(36) NOT NULL,
    team_index INT NOT NULL,
    player_id CHAR(36) NOT NULL,
    roster_role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_match_roster_player UNIQUE (match_id, player_id),
    INDEX idx_roster_championship (championship_id),
    INDEX idx_roster_match_team (match_id, team_index)
) ENGINE=InnoDB;

CREATE TABLE match_substitutions (
    id CHAR(36) PRIMARY KEY,
    championship_id CHAR(36) NOT NULL,
    match_id CHAR(36) NOT NULL,
    team_index INT NOT NULL,
    player_out_id CHAR(36) NOT NULL,
    player_in_id CHAR(36) NOT NULL,
    match_minute INT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_substitution_championship (championship_id),
    INDEX idx_substitution_match_team (match_id, team_index)
) ENGINE=InnoDB;

CREATE TABLE match_cards (
    id CHAR(36) PRIMARY KEY,
    championship_id CHAR(36) NOT NULL,
    match_id CHAR(36) NOT NULL,
    team_index INT NOT NULL,
    player_id CHAR(36) NOT NULL,
    card_type VARCHAR(30) NOT NULL,
    match_minute INT,
    reason VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_card_championship_player (championship_id, player_id),
    INDEX idx_card_match (match_id)
) ENGINE=InnoDB;

CREATE TABLE player_suspensions (
    id CHAR(36) PRIMARY KEY,
    championship_id CHAR(36) NOT NULL,
    player_id CHAR(36) NOT NULL,
    team_index INT NOT NULL,
    source_match_id CHAR(36) NOT NULL,
    source_card_id CHAR(36) NOT NULL,
    suspension_reason VARCHAR(40) NOT NULL,
    total_matches INT NOT NULL,
    remaining_matches INT NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_suspension_source_card UNIQUE (source_card_id),
    INDEX idx_suspension_championship_player (championship_id, player_id),
    INDEX idx_suspension_active_team (championship_id, team_index, status)
) ENGINE=InnoDB;

CREATE TABLE disciplinary_appeals (
    id CHAR(36) PRIMARY KEY,
    championship_id CHAR(36) NOT NULL,
    suspension_id CHAR(36) NOT NULL,
    reason VARCHAR(1000) NOT NULL,
    status VARCHAR(20) NOT NULL,
    decision_notes VARCHAR(1000),
    decided_by VARCHAR(180),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    decided_at TIMESTAMP NULL,
    INDEX idx_appeal_championship (championship_id),
    INDEX idx_appeal_suspension (suspension_id)
) ENGINE=InnoDB;
