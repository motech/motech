-- adds nonEditable column ---

DO
$$
BEGIN
    IF NOT EXISTS (SELECT column_name
            FROM information_schema.columns
            WHERE table_schema = current_schema()
            AND table_name = 'Field'
            AND column_name = 'nonEditable')
    THEN
        ALTER TABLE "Field" ADD "nonEditable" boolean NOT NULL DEFAULT false;
    END IF;
END
$$
