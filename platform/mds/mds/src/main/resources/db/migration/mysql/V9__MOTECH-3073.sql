-- extend the draft with fields to remove and required changes--

CREATE TABLE EntityDraft_fieldsToRemove (
  id_OID bigint(20) NOT NULL,
  fieldName varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (id_OID, fieldName),
  KEY EntityDraft_fieldsToRemove_key (id_OID),
  CONSTRAINT EntityDraft_fieldsToRemove_fk_Entity FOREIGN KEY (id_OID) REFERENCES Entity (id)
);

CREATE TABLE EntityDraft_fieldNameRequired (
  id_OID bigint(20) NOT NULL,
  fieldName varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  required varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (id_OID, fieldName),
  KEY EntityDraft_fieldNameRequired_KeyIdx1 (id_OID),
  CONSTRAINT EntityDraft_fieldNameRequired_FK1 FOREIGN KEY (id_OID) REFERENCES Entity (id)
);
