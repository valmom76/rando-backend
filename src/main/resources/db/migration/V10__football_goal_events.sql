CREATE TABLE match_goal_events (
    id CHAR(36) PRIMARY KEY,
    championship_id CHAR(36) NOT NULL,
    match_id CHAR(36) NOT NULL,
    scoring_team_index INT NOT NULL,
    scorer_team_index INT NOT NULL,
    player_id CHAR(36) NOT NULL,
    own_goal BOOLEAN NOT NULL DEFAULT FALSE,
    match_minute INT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_goal_championship_player (championship_id, player_id),
    INDEX idx_goal_match_team (match_id, scoring_team_index),
    INDEX idx_goal_championship (championship_id)
) ENGINE=InnoDB;
