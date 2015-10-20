-- Change empty values in MOTECH User openId column to NULL to avoid unique constraint violation

CREATE OR REPLACE FUNCTION convertMotechUserOpenId()

RETURNS void AS $$
BEGIN

IF EXISTS (SELECT * FROM information_schema.tables WHERE table_name = 'MOTECH_WEB_SECURITY_MOTECHUSER') THEN

UPDATE "MOTECH_WEB_SECURITY_MOTECHUSER"
SET "openId" = NULL
WHERE "openId" = '';

END IF;

END;

$$ LANGUAGE plpgsql;

SELECT convertMotechUserOpenId();

DROP FUNCTION convertMotechUserOpenId();