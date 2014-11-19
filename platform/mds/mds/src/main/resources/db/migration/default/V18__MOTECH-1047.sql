-- Adds ManyToMany relationship type ---

INSERT INTO "Type"
SELECT "id" + 1, 'mds.field.description.relationship.manyToMany','mds.field.relationship.manyToMany','manyToManyRelationship','org.motechproject.mds.domain.ManyToManyRelationship'
FROM "Type"
ORDER BY "id" DESC
LIMIT 1;

UPDATE "SEQUENCE_TABLE"
SET "NEXT_VAL" = (SELECT "id" + 1 FROM "Type" ORDER BY "id" DESC LIMIT 1)
WHERE "SEQUENCE_NAME" LIKE 'org.motechproject.mds.domain.Type';

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 0
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'org.motechproject.mds.domain.ManyToManyRelationship' AND ts."name" LIKE 'mds.form.label.cascadePersist';

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 1
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'org.motechproject.mds.domain.ManyToManyRelationship' AND ts."name" LIKE 'mds.form.label.cascadeUpdate';

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 2
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'org.motechproject.mds.domain.ManyToManyRelationship' AND ts."name" LIKE 'mds.form.label.cascadeDelete';