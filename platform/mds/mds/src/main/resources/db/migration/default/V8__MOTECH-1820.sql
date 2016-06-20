-- Adding new typesettings for the display of related fields

INSERT INTO "TypeSetting"
SELECT (ts."id" + 1), 'true', 'mds.form.label.expandByDefault', t."id"
FROM "TypeSetting" ts, "Type" t
WHERE t."typeClass" LIKE 'java.lang.Boolean'
ORDER BY ts."id" DESC
LIMIT 1;

INSERT INTO "TypeSetting"
SELECT (ts."id" + 1), 'true', 'mds.form.label.showCount', t."id"
FROM "TypeSetting" ts, "Type" t
WHERE t."typeClass" LIKE 'java.lang.Boolean'
ORDER BY ts."id" DESC
LIMIT 1;

INSERT INTO "TypeSetting"
SELECT (ts."id" + 1), 'true', 'mds.form.label.allowAddingNew', t."id"
FROM "TypeSetting" ts, "Type" t
WHERE t."typeClass" LIKE 'java.lang.Boolean'
ORDER BY ts."id" DESC
LIMIT 1;

INSERT INTO "TypeSetting"
SELECT (ts."id" + 1), 'true', 'mds.form.label.allowAddingExisting', t."id"
FROM "TypeSetting" ts, "Type" t
WHERE t."typeClass" LIKE 'java.lang.Boolean'
ORDER BY ts."id" DESC
LIMIT 1;

-- For OneToOne relationship

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 3
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'org.motechproject.mds.domain.OneToOneRelationship' AND ts."name" LIKE 'mds.form.label.expandByDefault';

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 4
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'org.motechproject.mds.domain.OneToOneRelationship' AND ts."name" LIKE 'mds.form.label.allowAddingNew';

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 5
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'org.motechproject.mds.domain.OneToOneRelationship' AND ts."name" LIKE 'mds.form.label.allowAddingExisting';

-- For ManyToOne relationship

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 3
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'org.motechproject.mds.domain.ManyToOneRelationship' AND ts."name" LIKE 'mds.form.label.expandByDefault';

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 4
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'org.motechproject.mds.domain.ManyToOneRelationship' AND ts."name" LIKE 'mds.form.label.allowAddingNew';

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 5
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'org.motechproject.mds.domain.ManyToOneRelationship' AND ts."name" LIKE 'mds.form.label.allowAddingExisting';

-- For OneToMany relationship

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 3
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'org.motechproject.mds.domain.OneToManyRelationship' AND ts."name" LIKE 'mds.form.label.expandByDefault';

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 4
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'org.motechproject.mds.domain.OneToManyRelationship' AND ts."name" LIKE 'mds.form.label.allowAddingNew';

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 5
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'org.motechproject.mds.domain.OneToManyRelationship' AND ts."name" LIKE 'mds.form.label.allowAddingExisting';

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 6
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'org.motechproject.mds.domain.OneToManyRelationship' AND ts."name" LIKE 'mds.form.label.showCount';

-- For ManyToMany relationship

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 3
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'org.motechproject.mds.domain.ManyToManyRelationship' AND ts."name" LIKE 'mds.form.label.expandByDefault';

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 4
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'org.motechproject.mds.domain.ManyToManyRelationship' AND ts."name" LIKE 'mds.form.label.allowAddingNew';

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 5
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'org.motechproject.mds.domain.ManyToManyRelationship' AND ts."name" LIKE 'mds.form.label.allowAddingExisting';

INSERT INTO "TYPE_TYPE_SETTING"
SELECT t."id", ts."id", 6
FROM "Type" t, "TypeSetting" ts
WHERE t."typeClass" LIKE 'org.motechproject.mds.domain.ManyToManyRelationship' AND ts."name" LIKE 'mds.form.label.showCount';