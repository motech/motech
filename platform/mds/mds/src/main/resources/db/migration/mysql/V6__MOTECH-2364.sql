-- Changing serviceInterface path from org.motechproject.openmrs19.tasks.OpenMRSActionProxyService to org.motechproject.openmrs.tasks.OpenMRSActionProxyService

DROP PROCEDURE IF EXISTS updateServiceInt;
DELIMITER //
CREATE PROCEDURE updateServiceInt()
BEGIN

    IF (SELECT count(*) FROM information_schema.columns WHERE table_name = 'MOTECH_TASKS_ACTIONEVENT') > 0 THEN

        Update motechdata.MOTECH_TASKS_ACTIONEVENT set serviceInterface='org.motechproject.openmrs.tasks.OpenMRSActionProxyService' where (serviceInterface='org.motechproject.openmrs19.tasks.OpenMRSActionProxyService' AND id <> 0);

    END IF;

END//

DELIMITER ;
CALL updateServiceInt();

DROP PROCEDURE IF EXISTS updateServiceInt;
