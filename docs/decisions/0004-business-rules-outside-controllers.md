# ADR 0004 — Business rules live in services and the domain model

- **Status:** Accepted
- **Date:** 2026-11-24
- **Deciders:** Project team

## Context

JavaFX controllers are easy to write but hard to unit test because they require a
JavaFX toolkit. If rules such as “a course cannot exceed capacity” live in a
controller, they cannot be tested without a display and are easily bypassed.

## Decision

- Rules about a single object live in the model (`Course.enroll`, `Course.setCapacity`).
- Rules spanning several objects live in `RegistrationService`
  (duplicate enrollment, deletion protection, persistence rollback).
- Controllers only translate user input, call services and show the resulting
  message.

## Consequences

- All rules are covered by JUnit tests without JavaFX.
- Screens contain little logic, which makes the code easier to review.
- Services depend on concrete repositories; introducing repository interfaces was
  judged unnecessary for the course scope (see ADR 0001).
