-- Changing serviceInterface path from org.motechproject.openmrs19.tasks.OpenMRSActionProxyService to org.motechproject.openmrs.tasks.OpenMRSActionProxyService

CREATE OR REPLACE FUNCTION updateServiceInt()
RETURNS void AS $$
BEGIN
    IF (SELECT count(*) FROM information_schema.columns WHERE table_name = 'MOTECH_TASKS_ACTIONEVENT') > 0 THEN

        UPDATE "MOTECH_TASKS_ACTIONEVENT" set "serviceInterface"='org.motechproject.openmrs.tasks.OpenMRSActionProxyService' where "serviceInterface"='org.motechproject.openmrs19.tasks.OpenMRSActionProxyService';

    END IF;

END

$$ LANGUAGE plpgsql;

SELECT updateServiceInt();

DROP FUNCTION updateServiceInt();