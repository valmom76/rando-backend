CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE tenant (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name VARCHAR(120) NOT NULL,
  slug VARCHAR(80) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE app_user (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  tenant_id UUID NOT NULL REFERENCES tenant(id) ON DELETE CASCADE,
  name VARCHAR(120) NOT NULL,
  email VARCHAR(180) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  role VARCHAR(30) NOT NULL DEFAULT 'ADMIN',
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE (tenant_id, email)
);

CREATE INDEX idx_app_user_tenant_email ON app_user(tenant_id, email);

CREATE TABLE player (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  tenant_id UUID NOT NULL REFERENCES tenant(id) ON DELETE CASCADE,
  name VARCHAR(120) NOT NULL,
  sex CHAR(1) NOT NULL CHECK (sex IN ('M','F')),
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_player_tenant_active ON player(tenant_id, active);

CREATE TABLE skill (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  tenant_id UUID NOT NULL REFERENCES tenant(id) ON DELETE CASCADE,
  name VARCHAR(120) NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE (tenant_id, code)
);

CREATE INDEX idx_skill_tenant_active ON skill(tenant_id, active);

CREATE TABLE player_skill_rating (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  tenant_id UUID NOT NULL REFERENCES tenant(id) ON DELETE CASCADE,
  player_id UUID NOT NULL REFERENCES player(id) ON DELETE CASCADE,
  skill_id UUID NOT NULL REFERENCES skill(id) ON DELETE CASCADE,
  rating SMALLINT NOT NULL CHECK (rating BETWEEN 0 AND 5),
  valid_from TIMESTAMPTZ NOT NULL DEFAULT now(),
  valid_to TIMESTAMPTZ NULL
);

CREATE UNIQUE INDEX ux_rating_current
ON player_skill_rating(tenant_id, player_id, skill_id)
WHERE valid_to IS NULL;

CREATE INDEX idx_rating_player_skill ON player_skill_rating(tenant_id, player_id, skill_id);

CREATE TABLE team_generation_session (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  tenant_id UUID NOT NULL REFERENCES tenant(id) ON DELETE CASCADE,
  mode VARCHAR(10) NOT NULL CHECK (mode IN ('TXT','DB')),
  created_by UUID NULL REFERENCES app_user(id),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  team_count INT NOT NULL CHECK (team_count > 0),
  players_per_team INT NOT NULL CHECK (players_per_team > 0),
  players_count INT NOT NULL CHECK (players_count >= 0),
  rules_json JSONB NOT NULL DEFAULT '{}'::jsonb,
  source_file_name VARCHAR(255) NULL
);

CREATE INDEX idx_session_tenant_created ON team_generation_session(tenant_id, created_at DESC);

CREATE TABLE generated_team (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  session_id UUID NOT NULL REFERENCES team_generation_session(id) ON DELETE CASCADE,
  team_index INT NOT NULL,
  name VARCHAR(120) NOT NULL
);

CREATE INDEX idx_team_session ON generated_team(session_id);

CREATE TABLE generated_team_player (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  team_id UUID NOT NULL REFERENCES generated_team(id) ON DELETE CASCADE,
  player_id UUID NOT NULL REFERENCES player(id) ON DELETE RESTRICT,
  sex_at_generation CHAR(1) NOT NULL CHECK (sex_at_generation IN ('M','F')),
  score_at_generation NUMERIC(10,4) NOT NULL,
  snapshot_json JSONB NOT NULL DEFAULT '{}'::jsonb
);

CREATE INDEX idx_team_player_team ON generated_team_player(team_id);

CREATE TABLE match_results (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    team_score INTEGER NOT NULL,
    opponent_score INTEGER NOT NULL,
    session_id UUID NOT NULL
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