-- adds bundleSymbolicName column ---

ALTER TABLE "Entity" ADD COLUMN "bundleSymbolicName" varchar(255) DEFAULT NULL;
INSERT INTO "Type" VALUES (71,'mds.field.description.discriminator','mds.field.discriminator','discriminatorName','org.motechproject.mds.domain.DiscriminatorAsType');
