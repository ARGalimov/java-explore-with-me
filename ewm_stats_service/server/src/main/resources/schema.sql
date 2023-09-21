DROP TABLE IF EXISTS stats;

CREATE TABLE IF NOT EXISTS stats (
    id        INT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    app       VARCHAR(256) NOT NULL,
    uri       VARCHAR(128) NOT NULL,
    ip        VARCHAR(32) NOT NULL,
    timestamp TIMESTAMP NOT NULL
);