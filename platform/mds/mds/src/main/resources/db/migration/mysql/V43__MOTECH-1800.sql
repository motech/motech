CREATE TABLE IF NOT EXISTS UserPreferences (
  className varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  username varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  gridRowsNumber int,
  PRIMARY KEY (className, username)
);

CREATE TABLE IF NOT EXISTS UserPreferences_selectedFields (
  className_OID varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  username_OID varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  selectedField bigint(20),
  IDX int(11),
  PRIMARY KEY (className_OID, username_OID, IDX),
  KEY UserPreferences_selectedFields_N49 (selectedField),
  KEY UserPreferences_selectedFields_N50 (className_OID, username_OID),
  CONSTRAINT UserPreferences_visibleFields_FK1 FOREIGN KEY (className_OID, username_OID) REFERENCES UserPreferences (className, username),
  CONSTRAINT UserPreferences_visibleFields_FK2 FOREIGN KEY (selectedField) REFERENCES Field (id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS UserPreferences_unselectedFields (
  className_OID varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  username_OID varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  unselectedField bigint(20),
  IDX int(11),
  PRIMARY KEY (className_OID, username_OID, IDX),
  KEY UserPreferences_unselectedFields_N49 (unselectedField),
  KEY UserPreferences_unselectedFields_N50 (className_OID, username_OID),
  CONSTRAINT UserPreferences_unselectedFields_FK1 FOREIGN KEY (className_OID, username_OID) REFERENCES UserPreferences (className, username),
  CONSTRAINT UserPreferences_unselectedFields_FK2 FOREIGN KEY (unselectedField) REFERENCES Field (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS ConfigSettings (
  id bigint(20),
  afterTimeUnit varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  afterTimeValue int(11),
  deleteMode varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  emptyTrash bit(1),
  PRIMARY KEY (id)
);

ALTER TABLE ConfigSettings add defaultGridSize int DEFAULT 50;
