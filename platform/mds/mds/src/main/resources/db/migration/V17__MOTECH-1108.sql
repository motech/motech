-- Adds ManyToOne relationship type ---

INSERT INTO Type
SELECT id + 1, 'mds.field.description.relationship.manyToOne','mds.field.relationship.manyToOne','manyToOneRelationship','org.motechproject.mds.domain.ManyToOneRelationship'
FROM Type
ORDER BY id DESC
LIMIT 1;

UPDATE SEQUENCE_TABLE
SET NEXT_VAL = (SELECT id + 1 FROM Type ORDER BY id DESC LIMIT 1)
WHERE SEQUENCE_NAME LIKE 'org.motechproject.mds.domain.Type';

INSERT INTO TYPE_TYPE_SETTING
SELECT t.id, ts.id, 0
FROM Type t, TypeSetting ts
WHERE t.typeClass LIKE 'org.motechproject.mds.domain.ManyToOneRelationship' AND ts.name LIKE 'mds.form.label.cascadePersist';

INSERT INTO TYPE_TYPE_SETTING
SELECT t.id, ts.id, 1
FROM Type t, TypeSetting ts
WHERE t.typeClass LIKE 'org.motechproject.mds.domain.ManyToOneRelationship' AND ts.name LIKE 'mds.form.label.cascadeUpdate';

INSERT INTO TYPE_TYPE_SETTING
SELECT t.id, ts.id, 2
FROM Type t, TypeSetting ts
WHERE t.typeClass LIKE 'org.motechproject.mds.domain.ManyToOneRelationship' AND ts.name LIKE 'mds.form.label.cascadeDelete';