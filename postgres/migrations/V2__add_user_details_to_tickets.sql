-- Migration: Add user details to tickets table
-- This adds email, firstName, and lastName fields to store additional user information
-- when a ticket is created, to avoid having to query Keycloak for every ticket request

-- Add new columns to tickets table (nullable for existing rows)
ALTER TABLE tickets
ADD COLUMN IF NOT EXISTS created_by_email VARCHAR(255),
ADD COLUMN IF NOT EXISTS created_by_first_name VARCHAR(100),
ADD COLUMN IF NOT EXISTS created_by_last_name VARCHAR(100);

-- Update existing rows to have empty strings instead of null
UPDATE tickets SET
    created_by_email = COALESCE(created_by_email, ''),
    created_by_first_name = COALESCE(created_by_first_name, ''),
    created_by_last_name = COALESCE(created_by_last_name, '');

-- Now make the columns NOT NULL with default values
ALTER TABLE tickets
ALTER COLUMN created_by_email SET DEFAULT '',
ALTER COLUMN created_by_email SET NOT NULL,
ALTER COLUMN created_by_first_name SET DEFAULT '',
ALTER COLUMN created_by_first_name SET NOT NULL,
ALTER COLUMN created_by_last_name SET DEFAULT '',
ALTER COLUMN created_by_last_name SET NOT NULL;

-- Create an index on created_by for faster lookups
CREATE INDEX IF NOT EXISTS idx_tickets_created_by ON tickets(created_by);

-- Add comment to document the new fields
COMMENT ON COLUMN tickets.created_by_email IS 'Email of the user who created the ticket (cached from Keycloak)';
COMMENT ON COLUMN tickets.created_by_first_name IS 'First name of the user who created the ticket (cached from Keycloak)';
COMMENT ON COLUMN tickets.created_by_last_name IS 'Last name of the user who created the ticket (cached from Keycloak)';
