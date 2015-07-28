-- add readOnlySecurityMembers table and readOnlySecurityMode column in Entity table --

ALTER TABLE Entity ADD readOnlySecurityMode varchar(255) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL;

CREATE TABLE IF NOT EXISTS Entity_readOnlySecurityMembers (
  Entity_OID bigint(20) NOT NULL,
  ReadOnlySecurityMember varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  PRIMARY KEY (Entity_OID, ReadOnlySecurityMember),
  KEY Entity_readOnlySecurityMembers_N49 (Entity_OID),
  CONSTRAINT Entity_readOnlySecurityMembers_FK1 FOREIGN KEY (Entity_OID) REFERENCES Entity (id)
);