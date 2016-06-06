-- Added auto increment sequence

ALTER TABLE "MigrationMapping" DROP COLUMN "flywayMigrationVersion";
ALTER TABLE "MigrationMapping" ADD COLUMN "flywayMigrationVersion" serial NOT NULL;
ALTER TABLE "MigrationMapping" ADD PRIMARY KEY ("flywayMigrationVersion")