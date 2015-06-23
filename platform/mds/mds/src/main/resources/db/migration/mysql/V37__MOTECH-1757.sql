--
-- Change combobox typeClass and defaultName
--

UPDATE Type
SET typeClass = "java.util.Collection", defaultName = "collection"
WHERE displayName LIKE "mds.field.combobox";
