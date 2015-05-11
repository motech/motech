-- adds nonEditable column ---

DELIMITER $$
CREATE PROCEDURE insert_non_editable_column()
BEGIN
    IF NOT EXISTS (SELECT column_name
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE table_schema = DATABASE()
            AND table_name = "Field"
            AND column_name = "nonEditable")
    THEN
        ALTER TABLE Field add nonEditable bit(1) NOT NULL default 0;
    END IF;
END
$$

DELIMITER ;
CALL insert_non_editable_column();
DROP PROCEDURE insert_non_editable_column;
