-- Changing serviceInterface path from org.motechproject.openmrs19.tasks.OpenMRSActionProxyService to org.motechproject.openmrs.tasks.OpenMRSActionProxyService

CREATE TABLE IF NOT EXISTS motechdata.MOTECH_TASKS_ACTIONEVENT (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	serviceInterface varchar(255),
	serviceMethod varchar(255),
	serviceMethodCallManner varchar(255),
	creationDate datetime,
	creator varchar(255),
	description varchar(255),
	displayName varchar(255),
	modificationDate datetime,
	modifiedBy varchar(255),
	name varchar(255),
	owner varchar(255),
	subject varchar(255),
	actionTaskEvents_id_OWN bigint(20),
	actionTaskEvents_INTEGER_IDX int(11),
	PRIMARY KEY (id)
);

UPDATE motechdata.MOTECH_TASKS_ACTIONEVENT SET serviceInterface='org.motechproject.openmrs.tasks.OpenMRSActionProxyService' WHERE serviceInterface LIKE 'org.motechproject.openmrs19.tasks.OpenMRSActionProxyService'
