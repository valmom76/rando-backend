-- V4__create_match_results.sql

-- Tabela de resultados de partidas
CREATE TABLE match_results (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    team_score INTEGER NOT NULL,
    opponent_score INTEGER NOT NULL
);

-- Tabela de relacionamento entre resultados e jogadores do time vencedor
CREATE TABLE match_result_players (
    match_result_id UUID NOT NULL,
    player_id UUID NOT NULL,
    PRIMARY KEY (match_result_id, player_id),
    FOREIGN KEY (match_result_id) REFERENCES match_results(id) ON DELETE CASCADE,
    FOREIGN KEY (player_id) REFERENCES player(id) ON DELETE CASCADE
);

-- Índices para consultas comuns
CREATE INDEX idx_match_results_tenant_id ON match_results(tenant_id);
CREATE INDEX idx_match_results_created_at ON match_results(created_at);
CREATE INDEX idx_match_result_players_player_id ON match_result_players(player_id);