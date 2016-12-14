-- extend the draft with fields to remove and required changes--

CREATE TABLE "EntityDraft_fieldsToRemove" (
  "id_OID" bigint NOT NULL,
  "fieldName" varchar(255) NOT NULL,
  PRIMARY KEY ("id_OID", "fieldName"),
  CONSTRAINT "EntityDraft_fieldsToRemove_fk_Entity" FOREIGN KEY ("id_OID") REFERENCES "Entity" ("id")
);

CREATE TABLE "EntityDraft_fieldNameRequired" (
  "id_OID" bigint NOT NULL,
  "fieldName" varchar(255) NOT NULL,
  "required" varchar(255) DEFAULT NULL,
  PRIMARY KEY ("id_OID", "fieldName"),
  CONSTRAINT "EntityDraft_fieldNameRequired_FK1" FOREIGN KEY ("id_OID") REFERENCES "Entity" ("id")
);
CREATE INDEX "EntityDraft_fieldNameRequired_KeyIdx1" ON "EntityDraft_fieldNameRequired" ("id_OID");