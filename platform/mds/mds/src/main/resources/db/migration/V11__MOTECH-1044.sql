INSERT INTO Type
SELECT id + 1, 'mds.field.description.relationship.oneToOne','mds.field.relationship.oneToOne','oneToOneRelationshipName','org.motechproject.mds.domain.OneToOneRelationship'
FROM Type
ORDER BY id DESC
LIMIT 1;

UPDATE SEQUENCE_TABLE
SET NEXT_VAL = (SELECT id + 1 FROM Type ORDER BY id DESC LIMIT 1)
WHERE SEQUENCE_NAME LIKE 'org.motechproject.mds.domain.Type';