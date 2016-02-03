--
-- Add new types to MDS
--
-- Adding Float to Type table
--
INSERT INTO Type
SELECT id + 1, 'mds.field.description.float','mds.field.float','float','java.lang.Float'
FROM Type
ORDER BY id DESC
LIMIT 1;

-- Adding Short to Type table
--
INSERT INTO Type
SELECT id + 1, 'mds.field.description.short','mds.field.short','short','java.lang.Short'
FROM Type
ORDER BY id DESC
LIMIT 1;

-- Adding Character to Type table
--
INSERT INTO Type
SELECT id + 1, 'mds.field.description.character','mds.field.character','char','java.lang.Character'
FROM Type
ORDER BY id DESC
LIMIT 1;

-- Adding Type Validations to float

INSERT INTO TypeValidation
SELECT tv.id + 1, 'mds.field.validation.minValue', t.id
FROM TypeValidation tv, Type t
WHERE t.typeClass LIKE 'java.lang.Float'
ORDER BY tv.id DESC
LIMIT 1;

INSERT INTO TypeValidation
SELECT tv.id + 1, 'mds.field.validation.maxValue', t.id
FROM TypeValidation tv, Type t
WHERE t.typeClass LIKE 'java.lang.Float'
ORDER BY tv.id DESC
LIMIT 1;

-- Adding Type Validations to short

INSERT INTO TypeValidation
SELECT tv.id + 1, 'mds.field.validation.minValue', t.id
FROM TypeValidation tv, Type t
WHERE t.typeClass LIKE 'java.lang.Short'
ORDER BY tv.id DESC
LIMIT 1;

INSERT INTO TypeValidation
SELECT tv.id + 1, 'mds.field.validation.maxValue', t.id
FROM TypeValidation tv, Type t
WHERE t.typeClass LIKE 'java.lang.Short'
ORDER BY tv.id DESC
LIMIT 1;

-- Joining Type and TypeValidation tables using TYPE_TYPE_VALIDATION table for float, short

INSERT INTO TYPE_TYPE_VALIDATION
SELECT t.id, 12, 0
FROM Type t
WHERE t.typeClass LIKE 'java.lang.Float'
LIMIT 1;

INSERT INTO TYPE_TYPE_VALIDATION
SELECT t.id, 13, 1
FROM Type t
WHERE t.typeClass LIKE 'java.lang.Float'
LIMIT 1;

INSERT INTO TYPE_TYPE_VALIDATION
SELECT t.id, 3, 2
FROM Type t
WHERE t.typeClass LIKE 'java.lang.Float'
LIMIT 1;

INSERT INTO TYPE_TYPE_VALIDATION
SELECT t.id, 4, 3
FROM Type t
WHERE t.typeClass LIKE 'java.lang.Float'
LIMIT 1;

-- TYPE_TYPE_VALIDATION for Short

INSERT INTO TYPE_TYPE_VALIDATION
SELECT t.id, 14, 0
FROM Type t
WHERE t.typeClass LIKE 'java.lang.Short'
LIMIT 1;

INSERT INTO TYPE_TYPE_VALIDATION
SELECT t.id, 15, 1
FROM Type t
WHERE t.typeClass LIKE 'java.lang.Short'
LIMIT 1;

INSERT INTO TYPE_TYPE_VALIDATION
SELECT t.id, 3, 2
FROM Type t
WHERE t.typeClass LIKE 'java.lang.Short'
LIMIT 1;

INSERT INTO TYPE_TYPE_VALIDATION
SELECT t.id, 4, 3
FROM Type t
WHERE t.typeClass LIKE 'java.lang.Short'
LIMIT 1;

-- Adding content to TypeValidation_annotations table

INSERT INTO TypeValidation_annotations VALUES (12,'javax.validation.constraints.DecimalMin',0), (12,'javax.validation.constraints.Min',1), (13,'javax.validation.constraints.DecimalMin',0), (13,'javax.validation.constraints.Min',1);

INSERT INTO TypeValidation_annotations VALUES (14,'javax.validation.constraints.DecimalMin',0), (14,'javax.validation.constraints.Min',1), (15,'javax.validation.constraints.DecimalMin',0), (15,'javax.validation.constraints.Min',1);

-- Adding Settings to Float Type

INSERT INTO TYPE_TYPE_SETTING
SELECT t.id, ts.id, 0
FROM Type t, TypeSetting ts
WHERE t.typeClass LIKE 'java.lang.Float' AND ts.name LIKE 'mds.form.label.precision';

INSERT INTO TYPE_TYPE_SETTING
SELECT t.id, ts.id, 1
FROM Type t, TypeSetting ts
WHERE t.typeClass LIKE 'java.lang.Float' AND ts.name LIKE 'mds.form.label.scale';
