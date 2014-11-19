--
-- insert settings for relationship types
--

INSERT INTO "TypeSetting"
SELECT (ts."id" + 1), 'true', 'mds.form.label.cascadePersist', t."id"
FROM "TypeSetting" ts, "Type" t
WHERE t."typeClass" LIKE 'java.lang.Boolean'
ORDER BY ts."id" DESC
LIMIT 1;

INSERT INTO "TypeSetting"
SELECT (ts."id" + 1), 'true', 'mds.form.label.cascadeUpdate', t."id"
FROM "TypeSetting" ts, "Type" t
WHERE t."typeClass" LIKE 'java.lang.Boolean'
ORDER BY ts."id" DESC
LIMIT 1;

INSERT INTO "TypeSetting"
SELECT (ts."id" + 1), 'false', 'mds.form.label.cascadeDelete', t."id"
FROM "TypeSetting" ts, "Type" t
WHERE t."typeClass" LIKE 'java.lang.Boolean'
ORDER BY ts."id" DESC
LIMIT 1;

UPDATE "SEQUENCE_TABLE"
SET "NEXT_VAL" = (SELECT "id" + 1 FROM "TypeSetting" ORDER BY "id" DESC LIMIT 1)
WHERE "SEQUENCE_NAME" LIKE 'org.motechproject.mds.domain.TypeSetting';

--
-- connect settings with relationship types
--

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 0
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'org.motechproject.mds.domain.Relationship' AND ts."name" LIKE 'mds.form.label.cascadePersist';

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 1
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'org.motechproject.mds.domain.Relationship' AND ts."name" LIKE 'mds.form.label.cascadeUpdate';

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 2
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'org.motechproject.mds.domain.Relationship' AND ts."name" LIKE 'mds.form.label.cascadeDelete';

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 0
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'org.motechproject.mds.domain.OneToManyRelationship' AND ts."name" LIKE 'mds.form.label.cascadePersist';

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 1
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'org.motechproject.mds.domain.OneToManyRelationship' AND ts."name" LIKE 'mds.form.label.cascadeUpdate';

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 2
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'org.motechproject.mds.domain.OneToManyRelationship' AND ts."name" LIKE 'mds.form.label.cascadeDelete';