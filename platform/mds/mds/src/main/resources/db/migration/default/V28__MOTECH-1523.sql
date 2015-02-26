CREATE TABLE "SchemaChangeLock" (
    "id" bigint PRIMARY KEY,
    "lockId" int
);

INSERT INTO "SchemaChangeLock" VALUES (1, 1);
