ALTER TABLE tenant
    ADD COLUMN sport_type VARCHAR(20) NOT NULL DEFAULT 'VOLLEYBALL';

ALTER TABLE championships
    ADD COLUMN sport_type VARCHAR(20) NOT NULL DEFAULT 'VOLLEYBALL';

CREATE INDEX idx_tenant_sport_type ON tenant (sport_type);
CREATE INDEX idx_championship_sport_type ON championships (sport_type);
