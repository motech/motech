-- Rename Date to java.util.Date and LocalDate to Date

UPDATE Type
SET displayName = "mds.field.javaUtilDate"
WHERE typeClass LIKE "java.util.Date";

UPDATE Type
SET displayName = "mds.field.date",
defaultName = "date"
WHERE typeClass LIKE "org.joda.time.LocalDate";