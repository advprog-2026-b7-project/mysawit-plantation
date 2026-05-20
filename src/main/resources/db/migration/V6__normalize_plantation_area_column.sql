DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'plantations'
          AND column_name = 'area_hectare'
    ) AND NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'plantations'
          AND column_name = 'area'
    ) THEN
        ALTER TABLE plantations RENAME COLUMN area_hectare TO area;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'plantations'
          AND column_name = 'area'
    ) THEN
        ALTER TABLE plantations ADD COLUMN area DOUBLE PRECISION;
    END IF;

    ALTER TABLE plantations
        ALTER COLUMN area TYPE DOUBLE PRECISION USING area::DOUBLE PRECISION;

    UPDATE plantations
    SET area = 0
    WHERE area IS NULL;

    ALTER TABLE plantations
        ALTER COLUMN area SET NOT NULL;
END $$;
