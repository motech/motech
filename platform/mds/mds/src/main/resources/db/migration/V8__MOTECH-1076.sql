--
-- Bring back the Integer type to MDS
--

-- First let's change the class responsible for handling the Integer type
UPDATE Type
SET typeClass = "java.lang.Integer"
WHERE typeClass LIKE "java.lang.Long";

-- Create a separate class for handling Long type
INSERT INTO Type
SELECT id + 1, 'mds.field.description.long','mds.field.long','long','java.lang.Long'
FROM Type
ORDER BY id DESC
LIMIT 1;

UPDATE SEQUENCE_TABLE
SET NEXT_VAL = (SELECT id + 1 FROM Type ORDER BY id DESC LIMIT 1)
WHERE SEQUENCE_NAME LIKE 'org.motechproject.mds.domain.Type';

-- The handler class for existing fields must stay the same (Long)
-- Assign fields of the Integer type to the Long type
UPDATE Field
SET type_id_OID = (SELECT id
                   FROM Type
                   WHERE displayName LIKE "mds.field.long")
WHERE type_id_OID = (SELECT id
                     FROM Type
                     WHERE displayName LIKE "mds.field.integer");

