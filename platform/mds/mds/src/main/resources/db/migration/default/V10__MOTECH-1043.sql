INSERT INTO "Type"
SELECT "id" + 1, 'mds.field.description.relationship','mds.field.relationship','relationshipName','org.motechproject.mds.domain.Relationship'
FROM "Type"
ORDER BY "id" DESC
LIMIT 1;

INSERT INTO "Type"
SELECT "id" + 1, 'mds.field.description.relationship.oneToMany','mds.field.relationship.oneToMany','oneToManyRelationshipName','org.motechproject.mds.domain.OneToManyRelationship'
FROM "Type"
ORDER BY "id" DESC
LIMIT 1;

UPDATE "SEQUENCE_TABLE"
SET "NEXT_VAL" = (SELECT "id" + 1 FROM "Type" ORDER BY "id" DESC LIMIT 1)
WHERE "SEQUENCE_NAME" LIKE 'org.motechproject.mds.domain.Type';