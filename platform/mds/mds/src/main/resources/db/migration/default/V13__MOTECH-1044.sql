INSERT INTO "Type"
SELECT "id" + 1, 'mds.field.description.relationship.oneToOne','mds.field.relationship.oneToOne','oneToOneRelationshipName','org.motechproject.mds.domain.OneToOneRelationship'
FROM "Type"
ORDER BY "id" DESC
LIMIT 1;

UPDATE "SEQUENCE_TABLE"
SET "NEXT_VAL" = (SELECT "id" + 1 FROM "Type" ORDER BY "id" DESC LIMIT 1)
WHERE "SEQUENCE_NAME" LIKE 'org.motechproject.mds.domain.Type';

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 0
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'org.motechproject.mds.domain.OneToOneRelationship' AND ts."name" LIKE 'mds.form.label.cascadePersist';

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 1
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'org.motechproject.mds.domain.OneToOneRelationship' AND ts."name" LIKE 'mds.form.label.cascadeUpdate';

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 2
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'org.motechproject.mds.domain.OneToOneRelationship' AND ts."name" LIKE 'mds.form.label.cascadeDelete';