--
-- Change value type for InSet and NotInSet validations
--

UPDATE "TypeValidation"
SET "TYPE_ID" = (SELECT "id"
               FROM "Type"
               WHERE "typeClass" LIKE 'java.lang.String')
WHERE "displayName" LIKE 'mds.field.validation.mustBeInSet';

UPDATE "TypeValidation"
SET "TYPE_ID" = (SELECT "id"
               FROM "Type"
               WHERE "typeClass" LIKE 'java.lang.String')
WHERE "displayName" LIKE 'mds.field.validation.cannotBeInSet';