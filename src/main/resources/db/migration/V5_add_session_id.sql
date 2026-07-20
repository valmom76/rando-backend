ALTER TABLE match_results ADD COLUMN session_id UUID;
ALTER TABLE match_results ADD CONSTRAINT fk_match_results_session FOREIGN KEY (session_id) REFERENCES team_generation_session(id);
CREATE INDEX idx_match_results_session ON match_results(session_id);