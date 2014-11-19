--
-- change column type from varchar to blob for 'value' column in 'FieldSetting' table
--

ALTER TABLE "FieldSetting" DROP COLUMN "value";
ALTER TABLE "FieldSetting" ADD COLUMN "value" TEXT DEFAULT NULL;