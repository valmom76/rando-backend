-- Os bancos existentes podem ter sido criados com CHAR, VARCHAR ou uma
-- collation diferente da configuração atual do servidor. O MySQL exige que
-- a coluna da FK tenha exatamente a mesma definição da coluna referenciada.
SET @championship_id_definition = (
    SELECT CONCAT(
        COLUMN_TYPE,
        CASE
            WHEN CHARACTER_SET_NAME IS NULL THEN ''
            ELSE CONCAT(
                ' CHARACTER SET ', CHARACTER_SET_NAME,
                ' COLLATE ', COLLATION_NAME
            )
        END
    )
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'championships'
      AND COLUMN_NAME = 'id'
);

SET @match_id_definition = (
    SELECT CONCAT(
        COLUMN_TYPE,
        CASE
            WHEN CHARACTER_SET_NAME IS NULL THEN ''
            ELSE CONCAT(
                ' CHARACTER SET ', CHARACTER_SET_NAME,
                ' COLLATE ', COLLATION_NAME
            )
        END
    )
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'championship_matches'
      AND COLUMN_NAME = 'id'
);

SET @create_penalty_shootouts = CONCAT(
    'CREATE TABLE match_penalty_shootouts (',
    'id CHAR(36) PRIMARY KEY, ',
    'championship_id ', @championship_id_definition, ' NOT NULL, ',
    'match_id ', @match_id_definition, ' NOT NULL, ',
    'home_team_index INT NOT NULL, ',
    'away_team_index INT NOT NULL, ',
    'home_score INT NOT NULL, ',
    'away_score INT NOT NULL, ',
    'winner_team_index INT NOT NULL, ',
    'created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, ',
    'CONSTRAINT uk_penalty_shootout_match UNIQUE (match_id), ',
    'CONSTRAINT fk_penalty_shootout_championship ',
        'FOREIGN KEY (championship_id) REFERENCES championships(id) ON DELETE CASCADE, ',
    'CONSTRAINT fk_penalty_shootout_match ',
        'FOREIGN KEY (match_id) REFERENCES championship_matches(id) ON DELETE CASCADE',
    ') ENGINE=InnoDB'
);

PREPARE create_penalty_shootouts_statement FROM @create_penalty_shootouts;
EXECUTE create_penalty_shootouts_statement;
DEALLOCATE PREPARE create_penalty_shootouts_statement;

CREATE INDEX idx_penalty_shootout_championship
    ON match_penalty_shootouts (championship_id);
