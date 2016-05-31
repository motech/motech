-- Changing serviceInterface path from org.motechproject.openmrs19.tasks.OpenMRSActionProxyService to org.motechproject.openmrs.tasks.OpenMRSActionProxyService

UPDATE "MOTECH_TASKS_ACTIONEVENT" set "serviceInterface"='org.motechproject.openmrs.tasks.OpenMRSActionProxyService' where ("serviceInterface"='org.motechproject.openmrs19.tasks.OpenMRSActionProxyService' AND "id" <> 0);
