CREATE TABLE "BundleFailsReport" (
  "id" bigint,
  "bundleRestartStatus" varchar(255) NOT NULL,
  "bundleSymbolicName" varchar(255) NOT NULL,
  "errorMessage" varchar(8096) NOT NULL,
  "nodeName" varchar(255),
  "reportDate" timestamp,
  PRIMARY KEY ("id")
);

ALTER TABLE "ConfigSettings" add "refreshModuleAfterTimeout" bit(1) DEFAULT 0;