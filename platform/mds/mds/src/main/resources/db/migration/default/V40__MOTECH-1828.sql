-- add readOnlySecurityMembers table and readOnlySecurityMode column in Entity table --

ALTER TABLE "Entity" ADD "readOnlySecurityMode" varchar(255) DEFAULT NULL;

CREATE TABLE IF NOT EXISTS "Entity_readOnlySecurityMembers" (
  "Entity_OID" bigint NOT NULL,
  "ReadOnlySecurityMember" varchar(255) NOT NULL,
  PRIMARY KEY ("Entity_OID","ReadOnlySecurityMember"),
  CONSTRAINT "Entity_readOnlySecurityMembers_FK1" FOREIGN KEY ("Entity_OID") REFERENCES "Entity" ("id")
);


