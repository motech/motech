CREATE TABLE IF NOT EXISTS SchemaChangeLock(
    id bigint(20) PRIMARY KEY,
    lockId int(1) UNIQUE
);

INSERT IGNORE INTO SchemaChangeLock VALUES (1, 1);
