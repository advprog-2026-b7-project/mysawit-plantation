DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'plantations'
          AND column_name = 'assigned_mandor_id'
    ) AND NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'plantations'
          AND column_name = 'mandor_id'
    ) THEN
        ALTER TABLE plantations RENAME COLUMN assigned_mandor_id TO mandor_id;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'plantations'
          AND column_name = 'coordinates_json'
    ) THEN
        ALTER TABLE plantations ADD COLUMN coordinates_json TEXT;
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'plantations'
          AND column_name = 'coordinates'
    ) THEN
        UPDATE plantations
        SET coordinates_json = coordinates::TEXT
        WHERE coordinates_json IS NULL;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'plantations'
          AND column_name = 'min_x'
    ) THEN
        ALTER TABLE plantations ADD COLUMN min_x INTEGER;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'plantations'
          AND column_name = 'min_y'
    ) THEN
        ALTER TABLE plantations ADD COLUMN min_y INTEGER;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'plantations'
          AND column_name = 'max_x'
    ) THEN
        ALTER TABLE plantations ADD COLUMN max_x INTEGER;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'plantations'
          AND column_name = 'max_y'
    ) THEN
        ALTER TABLE plantations ADD COLUMN max_y INTEGER;
    END IF;
END $$;

WITH parsed_coordinates AS (
    SELECT
        p.id,
        MIN((point.value ->> 0)::INTEGER) AS min_x,
        MIN((point.value ->> 1)::INTEGER) AS min_y,
        MAX((point.value ->> 0)::INTEGER) AS max_x,
        MAX((point.value ->> 1)::INTEGER) AS max_y
    FROM plantations p
    CROSS JOIN LATERAL jsonb_array_elements(p.coordinates_json::JSONB) AS point(value)
    WHERE p.coordinates_json IS NOT NULL
      AND jsonb_typeof(p.coordinates_json::JSONB) = 'array'
      AND jsonb_array_length(p.coordinates_json::JSONB) > 0
    GROUP BY p.id
)
UPDATE plantations p
SET min_x = COALESCE(p.min_x, parsed_coordinates.min_x),
    min_y = COALESCE(p.min_y, parsed_coordinates.min_y),
    max_x = COALESCE(p.max_x, parsed_coordinates.max_x),
    max_y = COALESCE(p.max_y, parsed_coordinates.max_y)
FROM parsed_coordinates
WHERE p.id = parsed_coordinates.id;

UPDATE plantations
SET coordinates_json = '[[0,0],[1,0],[1,1],[0,1]]'
WHERE coordinates_json IS NULL;

UPDATE plantations
SET min_x = COALESCE(min_x, 0),
    min_y = COALESCE(min_y, 0),
    max_x = COALESCE(max_x, 1),
    max_y = COALESCE(max_y, 1);

ALTER TABLE plantations
    ALTER COLUMN coordinates_json SET NOT NULL,
    ALTER COLUMN min_x SET NOT NULL,
    ALTER COLUMN min_y SET NOT NULL,
    ALTER COLUMN max_x SET NOT NULL,
    ALTER COLUMN max_y SET NOT NULL;
