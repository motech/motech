--
-- increase length of value for 'value' column in 'FieldSetting' table
--
ALTER TABLE FieldSetting
MODIFY COLUMN value varchar(65000);
