-- adds maxFetchDepth column ---

ALTER TABLE Entity ADD maxFetchDepth bigint(20) DEFAULT NULL;
