CREATE TABLE friendly_session_attendance_confirmations (
    session_id CHAR(36) PRIMARY KEY,
    tenant_id CHAR(36) NOT NULL,
    session_date DATE NOT NULL,
    confirmed_by CHAR(36) NOT NULL,
    confirmed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_attendance_confirmation_tenant_date (tenant_id, session_date)
) ENGINE=InnoDB;

CREATE TABLE friendly_session_attendance (
    id CHAR(36) PRIMARY KEY,
    tenant_id CHAR(36) NOT NULL,
    session_id CHAR(36) NOT NULL,
    player_id CHAR(36) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_friendly_attendance_session_player UNIQUE (session_id, player_id),
    INDEX idx_friendly_attendance_tenant_player (tenant_id, player_id),
    INDEX idx_friendly_attendance_session (session_id)
) ENGINE=InnoDB;
