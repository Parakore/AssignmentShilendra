
CREATE TABLE rcp_application (
    id VARCHAR(64) PRIMARY KEY,
    application_number VARCHAR(64) NOT NULL UNIQUE,
    tenant_id VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL,
    applicant_name VARCHAR(128) NOT NULL,
    applicant_mobile VARCHAR(20) NOT NULL,
    applicant_email VARCHAR(128),
    applicant_type VARCHAR(32) NOT NULL,
    road_type VARCHAR(32) NOT NULL,
    length_in_meters NUMERIC(10, 2) NOT NULL,
    width_in_meters NUMERIC(10, 2) NOT NULL,
    area_in_sqm BIGINT NOT NULL,
    duration_in_days INTEGER NOT NULL,
    proposed_start_date DATE NOT NULL,
    application_date DATE NOT NULL,
    location VARCHAR(255) NOT NULL,
    description TEXT,
    restoration_charge NUMERIC(14, 2) NOT NULL,
    permission_fee NUMERIC(14, 2) NOT NULL,
    urgency_surcharge NUMERIC(14, 2) NOT NULL,
    security_deposit NUMERIC(14, 2) NOT NULL,
    total_amount NUMERIC(14, 2) NOT NULL,
    created_by VARCHAR(64) NOT NULL,
    created_time BIGINT NOT NULL,
    last_modified_by VARCHAR(64) NOT NULL,
    last_modified_time BIGINT NOT NULL
);

CREATE INDEX idx_rcp_app_tenant_status ON rcp_application(tenant_id, status);
CREATE INDEX idx_rcp_app_tenant_mobile ON rcp_application(tenant_id, applicant_mobile);
CREATE INDEX idx_rcp_app_tenant_appnum ON rcp_application(tenant_id, application_number);

CREATE TABLE rcp_action_history (
    id VARCHAR(64) PRIMARY KEY,
    application_id VARCHAR(64) NOT NULL,
    application_number VARCHAR(64) NOT NULL,
    tenant_id VARCHAR(64) NOT NULL,
    action VARCHAR(32) NOT NULL,
    from_status VARCHAR(32),
    to_status VARCHAR(32) NOT NULL,
    actor_uuid VARCHAR(64) NOT NULL,
    actor_name VARCHAR(128) NOT NULL,
    actor_role VARCHAR(64) NOT NULL,
    comment TEXT,
    created_by VARCHAR(64) NOT NULL,
    created_time BIGINT NOT NULL,
    last_modified_by VARCHAR(64) NOT NULL,
    last_modified_time BIGINT NOT NULL,
    CONSTRAINT fk_rcp_history_app FOREIGN KEY (application_id) REFERENCES rcp_application(id) ON DELETE CASCADE
);

CREATE INDEX idx_rcp_history_app_id ON rcp_action_history(application_id);
CREATE INDEX idx_rcp_history_app_num ON rcp_action_history(tenant_id, application_number);

CREATE TABLE rcp_application_sequence (
    tenant_id VARCHAR(64) NOT NULL,
    financial_year VARCHAR(16) NOT NULL,
    last_sequence BIGINT NOT NULL,
    created_by VARCHAR(64) NOT NULL,
    created_time BIGINT NOT NULL,
    last_modified_by VARCHAR(64) NOT NULL,
    last_modified_time BIGINT NOT NULL,
    PRIMARY KEY (tenant_id, financial_year)
);
