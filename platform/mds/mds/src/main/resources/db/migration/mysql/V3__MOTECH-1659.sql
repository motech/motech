--
-- Add new types to MDS
--
-- Adding Float to Type table
--
INSERT INTO Type
SELECT id + 1, 'mds.field.description.float','mds.field.float','flo','java.lang.Float'
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

INSERT INTO TypeValidation VALUES (12,'mds.field.validation.minValue',23), (13,'mds.field.validation.maxValue',23);

-- Adding Type Validations to short

INSERT INTO TypeValidation VALUES (14,'mds.field.validation.minValue',24), (15,'mds.field.validation.maxValue',24);

-- Joining Type and TypeValidation tables using TYPE_TYPE_VALIDATION table for float, short

INSERT INTO TYPE_TYPE_VALIDATION VALUES (23, 12, 0), (23, 13, 1), (23, 3, 2), (23, 4, 3);

INSERT INTO TYPE_TYPE_VALIDATION VALUES (24, 14, 0), (24, 15, 1), (24, 3, 2), (24, 4, 3);

-- Adding content to TypeValidation_annotations table

INSERT INTO TypeValidation_annotations VALUES (12,'javax.validation.constraints.DecimalMin',0), (12,'javax.validation.constraints.Min',1), (13,'javax.validation.constraints.DecimalMin',0), (13,'javax.validation.constraints.Min',1);

INSERT INTO TypeValidation_annotations VALUES (14,'javax.validation.constraints.DecimalMin',0), (14,'javax.validation.constraints.Min',1), (15,'javax.validation.constraints.DecimalMin',0), (15,'javax.validation.constraints.Min',1);

-- Adding Settings to types

INSERT INTO TYPE_TYPE_SETTING VALUES (23, 1, 0), (23, 2, 1);
