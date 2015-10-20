-- conditionally adds maxFetchDepth column ---

DROP PROCEDURE IF EXISTS addMaxFetchDepth;

DELIMITER //
CREATE PROCEDURE addMaxFetchDepth()
BEGIN

IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_name = 'Entity' AND column_name = 'maxFetchDepth') THEN

ALTER TABLE Entity ADD maxFetchDepth bigint(20) DEFAULT NULL;

END IF;

END//

DELIMITER ;

CALL addMaxFetchDepth();

DROP PROCEDURE IF EXISTS addMaxFetchDepth;