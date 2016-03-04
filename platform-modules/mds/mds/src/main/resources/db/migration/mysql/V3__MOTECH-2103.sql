DROP TABLE IF EXISTS UserPreferences_selectedFields;
DROP TABLE IF EXISTS UserPreferences_unselectedFields;

CREATE TABLE IF NOT EXISTS UserPreferences_selectedFields (
  className_OID varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  username_OID varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  selectedField bigint(20),
  PRIMARY KEY (className_OID, username_OID, selectedField),
  KEY UserPreferences_selectedFields_N49 (selectedField),
  KEY UserPreferences_selectedFields_N50 (className_OID, username_OID),
  CONSTRAINT UserPreferences_visibleFields_FK1 FOREIGN KEY (className_OID, username_OID) REFERENCES UserPreferences (className, username),
  CONSTRAINT UserPreferences_visibleFields_FK2 FOREIGN KEY (selectedField) REFERENCES Field (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS UserPreferences_unselectedFields (
  className_OID varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  username_OID varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  unselectedField bigint(20),
  PRIMARY KEY (className_OID, username_OID, unselectedField),
  KEY UserPreferences_unselectedFields_N49 (unselectedField),
  KEY UserPreferences_unselectedFields_N50 (className_OID, username_OID),
  CONSTRAINT UserPreferences_unselectedFields_FK1 FOREIGN KEY (className_OID, username_OID) REFERENCES UserPreferences (className, username),
  CONSTRAINT UserPreferences_unselectedFields_FK2 FOREIGN KEY (unselectedField) REFERENCES Field (id) ON DELETE CASCADE
);