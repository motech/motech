--
-- insert settings for string type
--

INSERT INTO "TypeSetting"
SELECT (ts."id" + 1), 'false', 'mds.form.label.textarea', t."id"
FROM "TypeSetting" ts, "Type" t
WHERE t."typeClass" LIKE 'java.lang.Boolean'
ORDER BY ts."id" DESC
LIMIT 1;

UPDATE "SEQUENCE_TABLE"
SET "NEXT_VAL" = (SELECT "id" + 1 FROM "TypeSetting" ORDER BY "id" DESC LIMIT 1)
WHERE "SEQUENCE_NAME" LIKE 'org.motechproject.mds.domain.TypeSetting';

--
-- connect settings with string types
--

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 0
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'java.lang.String' AND ts."name" LIKE 'mds.form.label.textarea';