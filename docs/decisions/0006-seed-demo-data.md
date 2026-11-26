# ADR 0006 — Seed demo data only on an empty installation

- **Status:** Accepted
- **Date:** 2026-11-24
- **Deciders:** Project team

## Context

A freshly cloned repository has an empty `data/` directory. Starting with a blank
catalogue makes the dashboard and enrollment screens harder to evaluate, but
hard-coding records in controllers would violate the layering rules.

## Decision

`SampleDataInitializer` inserts a small demo data set (3 students, 4 courses, 4
enrollments) through the normal services, and only when both the student and course
lists are empty. Runtime CSV files are ignored by Git.

## Consequences

- The first launch shows meaningful statistics and enrollments.
- Demo data is created through the same validation as user data.
- Deleting the CSV files resets the application to the demo state.
- A real deployment could disable the initializer by removing the single call in
  `AppContext.bootstrap()`.
