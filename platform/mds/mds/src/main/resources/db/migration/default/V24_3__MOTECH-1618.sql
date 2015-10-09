-- adds maxFetchDepth column ---

ALTER TABLE "Entity" ADD COLUMN "maxFetchDepth" bigint DEFAULT NULL;
