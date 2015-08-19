-- adds bundleSymbolicName column ---

ALTER TABLE "Entity" ADD COLUMN "bundleSymbolicName" varchar(255) DEFAULT NULL;
