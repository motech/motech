--
-- Merge Integer and Long types into one type named Integer but handled by Long type class
--

-- First let's change the class responsible for handling the Integer type
UPDATE "Type"
SET "typeClass" = 'java.lang.Long'
WHERE "typeClass" LIKE 'java.lang.Integer';

-- Assign all fields of long type to integer type
UPDATE "Field"
SET "type_id_OID" = (SELECT "id"
                   FROM "Type"
                   WHERE "displayName" LIKE 'mds.field.integer')
WHERE "type_id_OID" = (SELECT "id"
                     FROM "Type"
                     WHERE "displayName" LIKE 'mds.field.long');

-- Now we can remove the Long type from the database
DELETE FROM "Type"
WHERE "displayName" LIKE 'mds.field.long';