CREATE TABLE IF NOT EXISTS "SchemaChangeLock" (
    "id" bigint PRIMARY KEY,
    "lockId" int UNIQUE
);

INSERT INTO "SchemaChangeLock" ("id", "lockId")
SELECT 1, 1
WHERE
    NOT EXISTS (
        SELECT id FROM "SchemaChangeLock" WHERE id = 1
    );
