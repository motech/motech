-- adds nonDisplayable column ---

DELIMITER $$
CREATE PROCEDURE insert_non_displayable_column()
BEGIN
    IF NOT EXISTS (SELECT column_name
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE table_schema = DATABASE()
            AND table_name = "Field"
            AND column_name = "nonDisplayable")
    THEN
        ALTER TABLE Field add nonDisplayable bit(1) NOT NULL default 0;
    END IF;
END
$$

DELIMITER ;
CALL insert_non_displayable_column();
DROP PROCEDURE insert_non_displayable_column;
