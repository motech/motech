--
-- add locale type to the MDS
--

INSERT INTO Type
SELECT id + 1, 'mds.field.description.locale','mds.field.locale','locale','java.util.Locale'
FROM Type
ORDER BY id DESC
LIMIT 1;

UPDATE SEQUENCE_TABLE
SET NEXT_VAL = (SELECT id + 1 FROM Type ORDER BY id DESC LIMIT 1)
WHERE SEQUENCE_NAME LIKE 'org.motechproject.mds.domain.Type'
