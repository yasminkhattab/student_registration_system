# Project Overview

## University Student Course Registration System

| Item | Value |
| --- | --- |
| Course | Programming II (CS113) |
| University | Taibah University |
| Semester | First Semester 2026/2027 |
| Project type | University group project (4–5 students) |
| Total marks | 15 (CLO 2.1 = 5, 2.2 = 3, 2.3 = 2, 3.1 = 5) |
| Language / GUI | Java 21 LTS, JavaFX 21, FXML, CSS |
| Build tool | Apache Maven |
| Persistence | CSV files through `java.nio.file` |
| Testing | JUnit 5 (53 tests) |
| Official brief | [`reference/CS113_Project_Instructions.pdf`](../reference/CS113_Project_Instructions.pdf) |

## Purpose

The system is a desktop application that supports the day-to-day administration of
student course registration. Two roles use the same application:

- an **administrator** maintains students and courses and monitors enrollments;
- a **student** signs in with a student identifier, browses the catalogue and manages
  their own registrations.

The project is deliberately scoped as a Programming II assignment: it demonstrates
object-oriented design, event-driven programming, GUI construction, exception
handling and file operations without enterprise infrastructure.

## Learning objectives

The implementation is organised so that each course learning outcome is visible in
the code and can be explained during an individual assessment:

| CLO | Marks | What the project demonstrates |
| --- | --- | --- |
| CLO 2.1 — Object-Oriented Program Development | 5 | `Person` hierarchy, `Registrable` interface, encapsulation, polymorphism, method overloading/overriding, associations and aggregation, no-argument and parameterized constructors, package-private methods |
| CLO 2.2 — GUI and Event-Driven Programming | 3 | Seven FXML screens, image assets, controllers with `@FXML` handlers, input validation, user feedback, navigation |
| CLO 2.3 — Development Tools and Libraries | 2 | Maven build, JavaFX libraries, JUnit 5, Git, external CSS, CSV persistence, IDE import instructions |
| CLO 3.1 — Teamwork and Demonstration | 5 | Documented architecture, contribution log, reproducible local setup, pre-recorded presentation plan |

## Main features

### Administrator

- Dashboard with student, course and enrollment statistics.
- Add, edit, delete and search students.
- Add, edit and delete courses when they have no active enrollment.
- Inspect the enrolled students of any course and drop an enrollment when needed.

### Student

- Sign in with a student identifier (for example `STU-1001`).
- View personal information and current registrations.
- Browse and search the course catalogue, including seat availability.
- Enroll in an open course and drop a registered course.

### Business rules

- Student and course identifiers are unique and normalised to upper case.
- A student cannot enroll twice in the same course.
- Enrollment is refused when the course is full.
- Students and courses must exist before an enrollment can be created.
- Courses with active enrollments cannot be deleted; students with active
  enrollments must drop them first.
- A course capacity can never be reduced below its current enrollment.
- Dropping a course keeps a historical registration row with status `DROPPED`.

## Design principles

- **Layered structure** — FXML/CSS, controllers, services, domain model and CSV
  repositories are separate packages.
- **Business rules outside controllers** — all rules live in services and the domain
  model, which keeps them unit-testable.
- **No framework overkill** — no Spring, Hibernate, database server, REST layer or
  containerisation.
- **Understandable by undergraduates** — constructor injection, plain Java
  collections and hand-written CSV helpers instead of generated code.

## Documentation map

| Document | Content |
| --- | --- |
| [`README.md`](../README.md) | Quick start, commands and repository layout |
| [`REQUIREMENTS.md`](REQUIREMENTS.md) | Official requirements, rubric and traceability to code |
| [`PROJECT_REPORT_OUTLINE.md`](PROJECT_REPORT_OUTLINE.md) | Report structure and submission checklist |
| [`ARCHITECTURE.md`](ARCHITECTURE.md) | Layers, package responsibilities and data flow |
| [`UML_CLASS_DIAGRAM.md`](UML_CLASS_DIAGRAM.md) | Mermaid class diagram matching the code |
| [`DATABASE_AND_PERSISTENCE.md`](DATABASE_AND_PERSISTENCE.md) | CSV design, file format and failure handling |
| [`GUI_DESIGN.md`](GUI_DESIGN.md) | Screens, navigation map and UI conventions |
| [`TESTING_STRATEGY.md`](TESTING_STRATEGY.md) | Automated and manual test plan |
| [`INSTALLATION.md`](INSTALLATION.md) | Environment setup for this machine and teammates |
| [`DEVELOPMENT_GUIDE.md`](DEVELOPMENT_GUIDE.md) | Coding conventions and change workflow |
| [`TEAM_CONTRIBUTION.md`](TEAM_CONTRIBUTION.md) | Contribution template for the group |
| [`CHANGELOG.md`](CHANGELOG.md) | Version history |
| [`decisions/`](decisions/) | Short architecture decision records |
