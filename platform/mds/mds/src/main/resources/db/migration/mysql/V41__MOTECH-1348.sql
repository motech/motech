-- adds bundleSymbolicName column ---

ALTER TABLE Entity ADD bundleSymbolicName varchar(255) DEFAULT NULL;
