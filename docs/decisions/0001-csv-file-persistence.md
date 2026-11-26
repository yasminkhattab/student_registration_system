# ADR 0001 — Use CSV files for persistence

- **Status:** Accepted
- **Date:** 2026-11-24
- **Deciders:** Project team

## Context

The assignment requires file operations and exception handling. The application is a
single-user desktop demonstration used by 4–5 students and a marker on ordinary
laptops.

## Decision

Persist students, courses and registrations in three UTF-8 CSV files under `data/`,
accessed through `java.nio.file`. Provide a `FileStorageManager` for safe writes and
one repository per file.

## Consequences

- No installation or configuration step for teammates or the marker.
- `IOException` handling is explicit and visible in the code, which matches the
  course outcomes.
- No transactions, indexes or concurrent access; acceptable for a single-user
  desktop application.
- The CSV format is documented in `docs/DATABASE_AND_PERSISTENCE.md`.
