# ADR 0005 — One built-in administrator account

- **Status:** Accepted
- **Date:** 2026-11-24
- **Deciders:** Project team

## Context

The assignment asks for simple authentication appropriate to an OOP course, not a
security subsystem. Storing administrator password hashes would add concepts
(hashing, salting, password policy) that distract from the learning outcomes.

## Decision

- Students authenticate with their student identifier only.
- A single administrator account is defined in `AppConfig` (`ADM-001`).
- The demo password is `admin123`, overridable through the `SRS_ADMIN_PASSWORD`
  environment variable.
- Administrators are not stored in the CSV files.

## Consequences

- Clear role separation without a user-management screen.
- Credentials are not production-grade and this is stated in the README.
- Extending to multiple administrators later would require an administrator
  repository and a password field in the persistence layer.
