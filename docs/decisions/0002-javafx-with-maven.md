# ADR 0002 — JavaFX managed by Maven

- **Status:** Accepted
- **Date:** 2026-11-24
- **Deciders:** Project team

## Context

JavaFX is no longer bundled with the JDK. The course requires a graphical Java
application, and the assignment expects standard development tools.

## Decision

Declare `javafx-controls`, `javafx-fxml` and `javafx-graphics` as Maven dependencies
and run the application with `org.openjfx:javafx-maven-plugin`. Do **not** copy a
JavaFX SDK into the repository.

## Consequences

- Every teammate gets the same JavaFX version (21.0.13) from Maven Central.
- No manual module-path configuration and no IDE-specific setup instructions.
- The first build requires internet access to download dependencies.
- The project stays a normal Maven project that any IDE can import.
