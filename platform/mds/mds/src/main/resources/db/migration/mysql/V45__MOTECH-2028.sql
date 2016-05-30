-- add unique column in Entity table --

ALTER TABLE Field ADD uniqueField bit(1) DEFAULT 0;

-- extend the draft with unique changes --

CREATE TABLE EntityDraft_uniqueIndexesToDrop (
  id_OID bigint(20) NOT NULL,
  fieldName varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (id_OID, fieldName),
  KEY EntityDraft_uniqueIndexesToDrop_key (id_OID),
  CONSTRAINT EntityDraft_uniqueIndexesToDrop_fk_Entity FOREIGN KEY (id_OID) REFERENCES Entity (id)
);

