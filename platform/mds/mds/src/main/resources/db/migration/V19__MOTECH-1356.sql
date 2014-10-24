-- This migration changes length of the value column in FieldValidation ---

ALTER TABLE FieldValidation MODIFY value VARCHAR(1024);