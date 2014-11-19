-- This migration changes length of the value column in FieldValidation ---

-- ALTER TABLE "FieldValidation" ALTER "value" varchar(1024);

ALTER TABLE "FieldValidation" ALTER COLUMN "value" TYPE varchar(1024);