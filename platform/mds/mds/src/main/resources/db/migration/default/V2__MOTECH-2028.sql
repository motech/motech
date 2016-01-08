-- add unique column in Entity table --

ALTER TABLE "Field" ADD "uniqueField" boolean NOT NULL DEFAULT FALSE;

-- extend the draft with unique changes --

CREATE TABLE "EntityDraft_uniqueIndexesToDrop" (
  "id_OID" bigint NOT NULL,
  "fieldName" varchar(255) NOT NULL,
  PRIMARY KEY ("id_OID", "fieldName"),
  CONSTRAINT "EntityDraft_uniqueIndexesToDrop_fk_Entity" FOREIGN KEY ("id_OID") REFERENCES "Entity" ("id")
);
