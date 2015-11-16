CREATE TABLE IF NOT EXISTS UserPreferences (
  className varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  username varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  gridRowsNumber int,
  PRIMARY KEY (className, username)
);

CREATE TABLE IF NOT EXISTS UserPreferences_visibleFields (
  className_OID varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  username_OID varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  visibleField bigint(20),
  IDX int(11),
  PRIMARY KEY (className_OID, username_OID, IDX),
  KEY UserPreferences_visibleFields_N49 (visibleField),
  KEY UserPreferences_visibleFields_N50 (className_OID, username_OID),
  CONSTRAINT UserPreferences_visibleFields_FK1 FOREIGN KEY (className_OID, username_OID) REFERENCES UserPreferences (className, username),
  CONSTRAINT UserPreferences_visibleFields_FK2 FOREIGN KEY (visibleField) REFERENCES Field (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS ConfigSettings (
  id bigint(20),
  afterTimeUnit varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  afterTimeValue int(11),
  deleteMode varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  emptyTrash bit(1),
  PRIMARY KEY (id)
);

DELIMITER $$
CREATE PROCEDURE insert_default_grid_size_column()
BEGIN
    IF NOT EXISTS (SELECT column_name
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE table_schema = DATABASE()
            AND table_name = "ConfigSettings"
            AND column_name = "defaultGridSize")
    THEN
        ALTER TABLE ConfigSettings add defaultGridSize int DEFAULT 10;
    END IF;
END
$$

DELIMITER ;
CALL insert_default_grid_size_column();
DROP PROCEDURE insert_default_grid_size_column;