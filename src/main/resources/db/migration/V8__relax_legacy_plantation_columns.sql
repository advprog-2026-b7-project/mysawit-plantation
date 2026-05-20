DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'plantations'
          AND column_name = 'coordinates'
    ) THEN
        ALTER TABLE plantations ALTER COLUMN coordinates DROP NOT NULL;
    END IF;
END $$;
