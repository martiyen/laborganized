-- ==================================================================
-- This script inserts initial data into the database.
-- The structure follows a JOINED inheritance strategy, where:
--   - `Storeable` is the parent abstract entity (mapped to `storeable` table).
--   - `Container` extends `Storeable` (mapped to `containers` table).
--   - `Reagent` extends `Storeable` (mapped to `reagents` table).
--
-- Relationships:
--   - A `User` (from `users` table) owns multiple `Storeable` items.
--   - A `Container` can store other `Storeable` items (containers or reagents).
-- ==================================================================

-- Insert Users
INSERT INTO users (id, username, name, password_hash, email, created, last_updated, user_role)
VALUES
(1, 'admin', 'Admin User', 'hashed_password', 'admin@example.com', NOW(), NOW(), 'ADMIN'),
(2, 'jdoe', 'John Doe', 'hashed_password', 'johndoe@example.com', NOW(), NOW(), 'MANAGER'),
(3, 'asmith', 'Alice Smith', 'hashed_password', 'alice@example.com', NOW(), NOW(), 'MEMBER');

-- Insert parent Storeable (Base Table for Containers and Reagents)
INSERT INTO storeable (id, name, user_id, container_id)
VALUES (1, 'Fridge A', 1, null);

-- Insert Container (Extending Storeable)
INSERT INTO containers (id, temperature, capacity)
VALUES (1, 4.0, 100);

-- Insert child Storeables
INSERT INTO storeable (id, name, user_id, container_id)
VALUES
(2, 'Box 1', 2, 1),
(3, 'Box 2', 3, 1);

-- Insert child Containers
INSERT INTO containers (id, temperature, capacity)
VALUES
(2, -20.0, 200),
(3, 22.0, NULL);

-- Insert Reagents (Extending Storeables)
INSERT INTO storeable (id, name, user_id, container_id)
VALUES
(4, 'Ethanol 96%', 1, 2),
(5, 'Sodium Chloride', 2, 2),
(6, 'Agarose', 3, 3);

INSERT INTO reagents (id, supplier, reference, quantity, unit, concentration, expiration_date, comments)
VALUES
(4, 'Sigma-Aldrich', 'ETOH-96', 500, 'mL', '96%', '2026-01-01', 'Flammable'),
(5, 'Merck', 'NaCl-99', 1000, 'g', '99%', '2027-06-15', 'For buffer preparation'),
(6, 'Thermo Fisher', 'AGR-1.5%', 250, 'g', '1.5%', '2028-12-10', 'For gel electrophoresis');

