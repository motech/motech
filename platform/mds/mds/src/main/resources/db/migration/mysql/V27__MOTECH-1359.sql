SET GLOBAL innodb_file_format = BARRACUDA;
SET GLOBAL innodb_large_prefix = ON;
ALTER TABLE Entity ROW_FORMAT=DYNAMIC;
