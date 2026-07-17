CREATE TABLE system_info
(
    id           BIGSERIAL PRIMARY KEY,
    application  VARCHAR(100) NOT NULL,
    version      VARCHAR(50)  NOT NULL,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO system_info (application, version)
VALUES ('WareFlow', '0.0.1');