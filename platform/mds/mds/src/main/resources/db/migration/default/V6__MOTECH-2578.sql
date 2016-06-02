-- Added auto increment sequence

CREATE OR REPLACE FUNCTION createFlywayMigrationVersionSeq()
RETURNS void AS $$

BEGIN

    IF (SELECT COUNT(*) FROM "pg_class" WHERE "relname"='flyway_migration_version_seq') = 0 THEN

	CREATE SEQUENCE "flyway_migration_version_seq";

    END IF;

    ALTER TABLE "MigrationMapping" ALTER COLUMN "flywayMigrationVersion" SET DEFAULT nextval('flyway_migration_version_seq'::regclass);

END

$$ LANGUAGE plpgsql;

SELECT createFlywayMigrationVersionSeq();

DROP FUNCTION createFlywayMigrationVersionSeq();