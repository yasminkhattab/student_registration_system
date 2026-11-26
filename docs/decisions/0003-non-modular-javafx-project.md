# ADR 0003 — Keep the JavaFX project non-modular

- **Status:** Accepted
- **Date:** 2026-11-24
- **Deciders:** Project team

## Context

Java 9+ supports the module system (`module-info.java`). JavaFX and FXML work with
modules, but FXML reflection requires `opens` declarations for controller packages,
and module-path errors are a common source of confusion for second-semester students.

## Decision

Use the classpath (no `module-info.java`) and let `javafx-maven-plugin` place JavaFX
on the module path for the runtime. Keep application packages on the classpath.

## Consequences

- Simpler build and fewer runtime errors during the demonstration.
- No explicit module encapsulation; acceptable for a course project.
- The design can be migrated to modules later without changing domain code.
