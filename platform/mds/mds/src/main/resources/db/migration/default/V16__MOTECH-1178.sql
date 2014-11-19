-- Adjusts default names for some "Type"s.
-- We should be consistent with these names.
--

UPDATE "Type"
SET "defaultName" = 'map'
WHERE "defaultName" = 'mapName';

UPDATE "Type"
SET "defaultName" = 'blob'
WHERE "defaultName" = 'blobName';

UPDATE "Type"
SET "defaultName" = 'localDate'
WHERE "defaultName" = 'localDateName';

UPDATE "Type"
SET "defaultName" = 'relationship'
WHERE "defaultName" = 'relationshipName';

UPDATE "Type"
SET "defaultName" = 'oneToManyRelationship'
WHERE "defaultName" = 'oneToManyRelationshipName';

UPDATE "Type"
SET "defaultName" = 'oneToOneRelationship'
WHERE "defaultName" = 'oneToOneRelationshipName';