CREATE TABLE plantation_driver_assignments (
    id UUID PRIMARY KEY,
    plantation_id UUID NOT NULL,
    driver_id UUID NOT NULL,
    driver_name VARCHAR(255) NOT NULL,
    assigned_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_driver_assignment_plantation
        FOREIGN KEY (plantation_id)
            REFERENCES plantations (id)
            ON DELETE CASCADE,
    CONSTRAINT uq_plantation_driver_pair
        UNIQUE (plantation_id, driver_id)
);