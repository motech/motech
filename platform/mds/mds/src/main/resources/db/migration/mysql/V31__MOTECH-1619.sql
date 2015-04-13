-- adds securityOptionsModified column ---

ALTER TABLE Entity add securityOptionsModified bit(1) NOT NULL default 0;
