-- conditionally adds maxFetchDepth column ---

CREATE OR REPLACE FUNCTION addMaxFetch()

RETURNS void AS $$
BEGIN

IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_name = 'Entity' AND column_name = 'maxFetchDepth') THEN

ALTER TABLE "Entity" ADD COLUMN "maxFetchDepth" bigint DEFAULT NULL;

END IF;

END;

$$ LANGUAGE plpgsql;

SELECT addMaxFetch();

DROP FUNCTION addMaxFetch();