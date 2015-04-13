-- adds securityOptionsModified column ---

ALTER TABLE "Entity" ADD "securityOptionsModified" boolean NOT NULL DEFAULT false;
