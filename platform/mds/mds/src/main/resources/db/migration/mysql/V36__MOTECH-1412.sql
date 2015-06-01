CREATE TABLE MigrationMapping (
  flywayMigrationVersion int NOT NULL,
  moduleMigrationVersion int NOT NULL,
  moduleName varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  PRIMARY KEY (flywayMigrationVersion)
);