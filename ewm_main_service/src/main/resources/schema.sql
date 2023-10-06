DROP TABLE IF EXISTS compilation_events cascade;
DROP TABLE IF EXISTS compilations cascade;
DROP TABLE IF EXISTS requests cascade;
DROP TABLE IF EXISTS events cascade;
DROP TABLE IF EXISTS locations cascade;
DROP TABLE IF EXISTS categories cascade;
DROP TABLE IF EXISTS users cascade;

CREATE TABLE IF NOT EXISTS users (
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    name  VARCHAR(255)                            NOT NULL,
    email VARCHAR(255) UNIQUE                     NOT NULL
);

CREATE TABLE IF NOT EXISTS categories (
    id   INT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    name VARCHAR(128) UNIQUE                  NOT NULL
);

CREATE TABLE IF NOT EXISTS events (
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    annotation         VARCHAR(2048)                           NOT NULL,
    category_id        BIGINT                                  NOT NULL,
    description        VARCHAR(7168)                           NOT NULL,
    created            TIMESTAMP                               NOT NULL,
    event_date         TIMESTAMP                               NOT NULL,
    published          TIMESTAMP,
    paid               BOOLEAN                                 NOT NULL,
    participant_limit  INT                                     NOT NULL,
    request_moderation BOOLEAN                                 NOT NULL,
    title              VARCHAR(128)                            NOT NULL,
    initiator_id       BIGINT                                  NOT NULL,
    location_id        BIGINT                                  NOT NULL,
    state              VARCHAR(32)                             NOT NULL,
    CONSTRAINT events_users_null_fk FOREIGN KEY (initiator_id) REFERENCES users (id),
    CONSTRAINT events_categories_null_fk FOREIGN KEY (category_id) REFERENCES categories (id)
);

CREATE TABLE IF NOT EXISTS locations (
    id  BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    lat FLOAT                                   NOT NULL,
    lon FLOAT                                   NOT NULL
);

CREATE TABLE IF NOT EXISTS requests (
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    status       VARCHAR(32)                             NOT NULL,
    created      TIMESTAMP                               NOT NULL,
    event_id     BIGINT                                  NOT NULL,
    requester_id BIGINT                                  NOT NULL,
    CONSTRAINT requests_users_null_fk FOREIGN KEY (requester_id) REFERENCES users (id),
    CONSTRAINT requests_events_null_fk FOREIGN KEY (event_id) REFERENCES events (id)
);

CREATE TABLE IF NOT EXISTS compilations (
    id     BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    title  VARCHAR(128)                            NOT NULL,
    pinned BOOLEAN                                 NOT NULL
);

CREATE TABLE IF NOT EXISTS compilation_events (
    compilation_id BIGINT NOT NULL,
    event_id       BIGINT NOT NULL,
    CONSTRAINT compilation_events_null_fk FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT events_compilation_null_fk FOREIGN KEY (compilation_id) REFERENCES compilations (id)
);