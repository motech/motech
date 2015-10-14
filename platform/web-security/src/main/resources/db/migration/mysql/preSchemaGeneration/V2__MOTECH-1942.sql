-- Change empty values in MOTECH User openId column to NULL to avoid unique constraint violation

DROP PROCEDURE IF EXISTS convertMotechUserOpenId;

DELIMITER //
CREATE PROCEDURE convertMotechUserOpenId()
BEGIN

IF EXISTS (SELECT * FROM information_schema.tables WHERE table_name = 'MOTECH_WEB_SECURITY_MOTECHUSER') THEN

UPDATE MOTECH_WEB_SECURITY_MOTECHUSER
SET openId = NULL
WHERE openId = "";

END IF;

END//

DELIMITER ;

CALL convertMotechUserOpenId();

DROP PROCEDURE IF EXISTS convertMotechUserOpenId;