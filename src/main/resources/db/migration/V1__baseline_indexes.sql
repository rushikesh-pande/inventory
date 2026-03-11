-- Database Optimisation Enhancement: Baseline Performance Indexes
-- Service: inventory
-- Version: V1 — initial performance indexes
-- Generated: 2026-03-11

-- ── Ensure baseline migration ────────────────────────────────────────────────
-- This script adds performance indexes to the existing schema.
-- All CREATE INDEX statements use IF NOT EXISTS (safe for re-run).

-- ── Standard performance indexes ─────────────────────────────────────────────

-- Status column (most queries filter by status)
-- CREATE INDEX IF NOT EXISTS idx_inventory_status ON inventorys (status);

-- Created date (pagination/sorting by date)
-- CREATE INDEX IF NOT EXISTS idx_inventory_created_at ON inventorys (created_at DESC);

-- Composite: status + created_at (covers most list queries)
-- CREATE INDEX IF NOT EXISTS idx_inventory_status_created
--     ON inventorys (status, created_at DESC);

-- Foreign key index (prevents full-table scan on joins)
-- CREATE INDEX IF NOT EXISTS idx_inventory_user_id ON inventorys (user_id);

-- ── H2 compatible placeholder ─────────────────────────────────────────────────
-- The actual CREATE INDEX statements are commented out above because
-- the exact table/column names depend on your @Entity definitions.
-- Uncomment and adapt to your schema before running in production.

-- Flyway baseline marker
SELECT 1 AS flyway_baseline_check;
