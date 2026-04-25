CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE EXTENSION IF NOT EXISTS "btree_gist";

CREATE TABLE clinics (
    id                 BIGSERIAL    PRIMARY KEY,
    name               VARCHAR(200) NOT NULL,
    slug               VARCHAR(80)  NOT NULL UNIQUE,
    timezone           VARCHAR(64)  NOT NULL DEFAULT 'Europe/Bucharest',
    phone              VARCHAR(32),
    email              VARCHAR(160),
    address            TEXT,
    slot_duration_min  SMALLINT     NOT NULL DEFAULT 30 CHECK (slot_duration_min BETWEEN 5 AND 240),
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at         TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted_at         TIMESTAMPTZ
);
CREATE INDEX idx_clinics_slug_active ON clinics(slug) WHERE deleted_at IS NULL;

CREATE TABLE users (
    id             BIGSERIAL    PRIMARY KEY,
    clinic_id      BIGINT       NOT NULL REFERENCES clinics(id) ON DELETE RESTRICT,
    email          VARCHAR(160) NOT NULL,
    password_hash  VARCHAR(255) NOT NULL,
    full_name      VARCHAR(160) NOT NULL,
    role           VARCHAR(20)  NOT NULL CHECK (role IN ('ADMIN','DOCTOR','STAFF')),
    is_active      BOOLEAN      NOT NULL DEFAULT TRUE,
    last_login_at  TIMESTAMPTZ,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted_at     TIMESTAMPTZ,
    CONSTRAINT uq_users_clinic_email UNIQUE (clinic_id, email)
);

CREATE TABLE working_hours (
    id           BIGSERIAL   PRIMARY KEY,
    clinic_id    BIGINT      NOT NULL REFERENCES clinics(id) ON DELETE CASCADE,
    day_of_week  SMALLINT    NOT NULL CHECK (day_of_week BETWEEN 0 AND 6),
    is_working   BOOLEAN     NOT NULL DEFAULT TRUE,
    start_time   TIME,
    end_time     TIME,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_wh_clinic_day UNIQUE (clinic_id, day_of_week),
    CONSTRAINT chk_wh_times CHECK (
        (is_working = FALSE) OR
        (start_time IS NOT NULL AND end_time IS NOT NULL AND start_time < end_time)
    )
);

CREATE TABLE breaks (
    id           BIGSERIAL   PRIMARY KEY,
    clinic_id    BIGINT      NOT NULL REFERENCES clinics(id) ON DELETE CASCADE,
    day_of_week  SMALLINT    NOT NULL CHECK (day_of_week BETWEEN 0 AND 6),
    start_time   TIME        NOT NULL,
    end_time     TIME        NOT NULL,
    label        VARCHAR(80),
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_break_times CHECK (start_time < end_time)
);
CREATE INDEX idx_breaks_clinic_day ON breaks(clinic_id, day_of_week);

CREATE TABLE appointments (
    id                  BIGSERIAL    PRIMARY KEY,
    clinic_id           BIGINT       NOT NULL REFERENCES clinics(id) ON DELETE RESTRICT,
    cancellation_token  UUID         NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    patient_name        VARCHAR(160) NOT NULL,
    patient_email       VARCHAR(160) NOT NULL,
    patient_phone       VARCHAR(32)  NOT NULL,
    notes               TEXT,
    starts_at           TIMESTAMPTZ  NOT NULL,
    ends_at             TIMESTAMPTZ  NOT NULL,
    status              VARCHAR(20)  NOT NULL DEFAULT 'CONFIRMED'
                          CHECK (status IN ('CONFIRMED','CANCELLED','NO_SHOW','COMPLETED')),
    cancelled_at        TIMESTAMPTZ,
    cancelled_by        VARCHAR(20)  CHECK (cancelled_by IN ('PATIENT','CLINIC','SYSTEM')),
    cancel_reason       TEXT,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT chk_appt_times CHECK (starts_at < ends_at)
);
CREATE INDEX idx_appt_clinic_starts ON appointments(clinic_id, starts_at);
CREATE INDEX idx_appt_patient_phone ON appointments(clinic_id, patient_phone);
CREATE INDEX idx_appt_patient_email ON appointments(clinic_id, patient_email);

ALTER TABLE appointments
  ADD CONSTRAINT no_overlap_confirmed
  EXCLUDE USING gist (
      clinic_id WITH =,
      tstzrange(starts_at, ends_at, '[)') WITH &&
  ) WHERE (status = 'CONFIRMED');

CREATE TABLE blocked_slots (
    id          BIGSERIAL    PRIMARY KEY,
    clinic_id   BIGINT       NOT NULL REFERENCES clinics(id) ON DELETE CASCADE,
    starts_at   TIMESTAMPTZ  NOT NULL,
    ends_at     TIMESTAMPTZ  NOT NULL,
    reason      VARCHAR(200),
    created_by  BIGINT       REFERENCES users(id) ON DELETE SET NULL,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT chk_block_times CHECK (starts_at < ends_at)
);
CREATE INDEX idx_blocked_clinic_starts ON blocked_slots(clinic_id, starts_at);

CREATE TABLE notification_jobs (
    id              BIGSERIAL    PRIMARY KEY,
    clinic_id       BIGINT       NOT NULL REFERENCES clinics(id) ON DELETE CASCADE,
    appointment_id  BIGINT       REFERENCES appointments(id) ON DELETE CASCADE,
    type            VARCHAR(10)  NOT NULL CHECK (type IN ('EMAIL','SMS')),
    channel         VARCHAR(20)  NOT NULL CHECK (channel IN ('CONFIRMATION','REMINDER_24H','REMINDER_2H','CANCELLATION')),
    status          VARCHAR(10)  NOT NULL DEFAULT 'PENDING'
                      CHECK (status IN ('PENDING','SENT','FAILED','SKIPPED')),
    recipient       VARCHAR(160) NOT NULL,
    payload         JSONB        NOT NULL,
    send_at         TIMESTAMPTZ  NOT NULL,
    sent_at         TIMESTAMPTZ,
    attempts        SMALLINT     NOT NULL DEFAULT 0,
    last_error      TEXT,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX idx_notif_due ON notification_jobs(send_at) WHERE status = 'PENDING';
CREATE INDEX idx_notif_appt ON notification_jobs(appointment_id);
CREATE UNIQUE INDEX uq_notif_appt_channel_type
    ON notification_jobs(appointment_id, channel, type)
    WHERE appointment_id IS NOT NULL;

CREATE TABLE refresh_tokens (
    id              BIGSERIAL    PRIMARY KEY,
    user_id         BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash      VARCHAR(255) NOT NULL UNIQUE,
    issued_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    expires_at      TIMESTAMPTZ  NOT NULL,
    revoked_at      TIMESTAMPTZ,
    replaced_by_id  BIGINT       REFERENCES refresh_tokens(id) ON DELETE SET NULL,
    user_agent      VARCHAR(255),
    ip_address      INET
);

-- ShedLock table for distributed scheduler coordination
CREATE TABLE shedlock (
    name       VARCHAR(64)  NOT NULL,
    lock_until TIMESTAMPTZ  NOT NULL,
    locked_at  TIMESTAMPTZ  NOT NULL,
    locked_by  VARCHAR(255) NOT NULL,
    CONSTRAINT pk_shedlock PRIMARY KEY (name)
);

-- Trigger function: auto-update updated_at on row modification
CREATE OR REPLACE FUNCTION set_updated_at() RETURNS TRIGGER AS $$
BEGIN NEW.updated_at = now(); RETURN NEW; END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_clinics_updated  BEFORE UPDATE ON clinics          FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE TRIGGER trg_users_updated    BEFORE UPDATE ON users             FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE TRIGGER trg_wh_updated       BEFORE UPDATE ON working_hours     FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE TRIGGER trg_appt_updated     BEFORE UPDATE ON appointments      FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE TRIGGER trg_notif_updated    BEFORE UPDATE ON notification_jobs FOR EACH ROW EXECUTE FUNCTION set_updated_at();
