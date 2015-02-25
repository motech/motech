CREATE TABLE SchemaChangeLock(
    id bigint(20) PRIMARY KEY,
    lockId int(1)
);

INSERT INTO SchemaChangeLock VALUES (1, 1);
