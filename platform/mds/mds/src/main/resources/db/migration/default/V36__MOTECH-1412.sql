CREATE TABLE "MigrationMapping" (
  "flywayMigrationVersion" int NOT NULL,
  "moduleMigrationVersion" int NOT NULL,
  "moduleName" varchar(255) NOT NULL,
  PRIMARY KEY ("flywayMigrationVersion")
);