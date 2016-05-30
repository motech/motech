CREATE TABLE "BundleFailsReport" (
  "id" bigint,
  "bundleRestartStatus" varchar(255) NOT NULL,
  "bundleSymbolicName" varchar(255) NOT NULL,
  "errorMessage" text NOT NULL,
  "nodeName" varchar(255),
  "reportDate" timestamp,
  PRIMARY KEY ("id")
);

ALTER TABLE "ConfigSettings" add "refreshModuleAfterTimeout" boolean DEFAULT FALSE;