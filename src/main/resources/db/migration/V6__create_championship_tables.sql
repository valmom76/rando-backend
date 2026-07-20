CREATE TABLE championships (
    id CHAR(36) PRIMARY KEY,
    tenant_id CHAR(36) NOT NULL,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    started_at TIMESTAMP NULL,
    ended_at TIMESTAMP NULL,
    team_count INT NOT NULL,
    groups_count INT NOT NULL,
    teams_per_group INT NOT NULL,
    qualified_per_group INT NOT NULL,
    matches_type VARCHAR(20) NOT NULL,
    points_per_win INT DEFAULT 3,
    status VARCHAR(20) NOT NULL,
    generation_session_id CHAR(36),
    FOREIGN KEY (tenant_id) REFERENCES tenant(id) ON DELETE CASCADE,
    FOREIGN KEY (generation_session_id) REFERENCES team_generation_session(id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE championship_teams (
    id CHAR(36) PRIMARY KEY,
    championship_id CHAR(36) NOT NULL,
    team_index INT NOT NULL,
    group_index INT,
    seed INT NOT NULL,
    initial_score DECIMAL(10,4),
    FOREIGN KEY (championship_id) REFERENCES championships(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE championship_matches (
    id CHAR(36) PRIMARY KEY,
    championship_id CHAR(36) NOT NULL,
    group_index INT,
    round INT NOT NULL,
    home_team_index INT NOT NULL,
    away_team_index INT NOT NULL,
    home_score INT,
    away_score INT,
    played BOOLEAN DEFAULT FALSE,
    match_date TIMESTAMP NULL,
    winner_team_index INT,
    match_result_id CHAR(36),
    FOREIGN KEY (championship_id) REFERENCES championships(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE championship_standings (
    id CHAR(36) PRIMARY KEY,
    championship_id CHAR(36) NOT NULL,
    team_index INT NOT NULL,
    group_index INT,
    points INT DEFAULT 0,
    played INT DEFAULT 0,
    wins INT DEFAULT 0,
    draws INT DEFAULT 0,
    losses INT DEFAULT 0,
    goals_for INT DEFAULT 0,
    goals_against INT DEFAULT 0,
    goals_difference INT DEFAULT 0,
    last_update TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (championship_id) REFERENCES championships(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_championship_tenant ON championships(tenant_id);
CREATE INDEX idx_championship_teams_championship ON championship_teams(championship_id);
CREATE INDEX idx_championship_matches_championship ON championship_matches(championship_id);
CREATE INDEX idx_championship_standings_championship ON championship_standings(championship_id);