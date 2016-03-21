INSERT INTO Type
SELECT id + 1, 'mds.field.description.uuid','mds.field.uuid','uuid','java.util.UUID'
FROM Type
ORDER BY id DESC
LIMIT 1;