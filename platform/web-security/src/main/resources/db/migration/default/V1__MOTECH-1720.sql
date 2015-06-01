CREATE OR REPLACE FUNCTION deleteActiveColumn()

RETURNS void AS $$
BEGIN

IF EXISTS (SELECT * FROM information_schema.columns WHERE table_name = 'MOTECH_WEB_SECURITY_MOTECHUSER' AND column_name = 'active') THEN

UPDATE "MOTECH_WEB_SECURITY_MOTECHUSER"
SET "userStatus" = 'ACTIVE'
WHERE "active" = TRUE;

UPDATE "MOTECH_WEB_SECURITY_MOTECHUSER"
SET "userStatus" = 'BLOCKED'
WHERE "active" = FALSE;

ALTER TABLE "MOTECH_WEB_SECURITY_MOTECHUSER" DROP COLUMN "active";

END IF;

IF EXISTS (SELECT * FROM information_schema.columns WHERE table_name = 'MOTECH_WEB_SECURITY_MOTECHUSER__HISTORY' AND column_name = 'active') THEN

ALTER TABLE "MOTECH_WEB_SECURITY_MOTECHUSER__HISTORY" DROP COLUMN "active";

END IF;

IF EXISTS (SELECT * FROM information_schema.columns WHERE table_name = 'MOTECH_WEB_SECURITY_MOTECHUSER__TRASH' AND column_name = 'active') THEN

ALTER TABLE "MOTECH_WEB_SECURITY_MOTECHUSER__TRASH" DROP COLUMN "active";

END IF;

END;

$$ LANGUAGE plpgsql;

SELECT deleteActiveColumn();

DROP FUNCTION deleteActiveColumn();