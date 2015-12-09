CREATE TABLE BundleFailsReport (
  id bigint(20),
  bundleRestartStatus varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  bundleSymbolicName varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  errorMessage mediumtext NOT NULL,
  nodeName	varchar(255) CHARACTER SET latin1 COLLATE latin1_bin,
  reportDate datetime,
  PRIMARY KEY (id)
);

ALTER TABLE ConfigSettings add refreshModuleAfterTimeout bit(1) DEFAULT 0;