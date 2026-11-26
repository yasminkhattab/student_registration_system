# Changelog

All notable changes to this project are documented in this file.
The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/) and the
project uses semantic versioning for its Maven artefact.

## [Unreleased]

### Added

- Added contributor guidance for the `main` / `develop` branch workflow, GitHub
  issue forms for bugs, features and documentation, a pull request template, and a
  Maven CI workflow for pull requests and shared branch pushes.
- The official brief (`reference/CS113_Project_Instructions.pdf`) is now the primary
  requirements source; `docs/REQUIREMENTS.md` matches its CLO marks (5/3/2/5) and
  rubric.
- No-argument constructors for `Student`, `Administrator`, `Course` and
  `Registration` (plus the existing protected no-argument `Person` constructor), as
  explicitly required by the brief.
- `university-mark.png` and `ImageView` elements on the login card and all six screen
  headers, satisfying the GUI "images" requirement.
- Package-private `toCsvLine(...)` methods in the three CSV repositories, exercising
  the package-private access level and making row formatting directly testable.
- `EntityConstructorsTest` and `RepositoryCsvFormatTest` (8 additional tests; 53 in
  total).
- `docs/PROJECT_REPORT_OUTLINE.md` with the official cover page, Sections 1–4,
  presentation plan and submission checklist.
- `scripts/package-submission.sh` to build a `CS113_<Section>_Project_Group<X>.zip`
  source archive without `target/`, data files or IDE metadata.
- IDE import instructions for IntelliJ IDEA, Eclipse and NetBeans (the approved IDEs
  named in the brief).

### Changed

- Clarified the Java 21, JavaFX 21.0.13 and JavaFX Maven Plugin 0.0.8 run
  configuration in the README and installation guide. Added troubleshooting for
  Maven plugin resolution, unwritable JavaFX caches, missing JavaFX modules, FXML
  startup errors and inaccessible graphical displays. Confirmed that the existing
  plugin declaration is correct; the observed launch failure in the restricted
  execution session was display access, not a missing Maven plugin.
- GUI image declarations use the nested `<Image url="..."/>` form; a plain
  `image="..."` attribute is not coercible to `javafx.scene.image.Image`.
- `SceneNavigator` prints the real FXML load failure to standard error, and
  `AlertHelper` only assigns an owner window that already has a scene, so a startup
  error can no longer be masked by a `NullPointerException` in the dialog.
- Documentation updated for the official deadline (12 December 2026, 11:59 PM),
  report/ZIP submission rules and the pre-recorded presentation.

### Planned

- Screenshots of every screen for the final report.
- Manual GUI test results recorded by the group.
- Final presentation script and recording.

## [0.1.0] - 2026-11-24

### Added

- Maven project for Java 21 with JavaFX 21 (controls, FXML, graphics) and JUnit 5.
- Layered package structure: `config`, `model`, `interfaces`, `service`,
  `repository`, `exception`, `controller`, `util`.
- Domain model: abstract `Person`, `Student`, `Administrator`, `Course`,
  `Registration`, `RegistrationStatus` and the `Registrable` interface.
- Business services: `StudentService`, `CourseService`, `RegistrationService` with
  validation, duplicate/capacity rules and deletion protection.
- CSV persistence (`students.csv`, `courses.csv`, `registrations.csv`) with atomic
  writes, `IOException` translation and start-up rehydration of enrollments.
- Seven FXML screens with controllers, shared stylesheet and screen navigation.
- Demo data seeding on an empty installation.
- Unit and persistence tests for validation, CSV handling and all services.
- Documentation: overview, architecture, UML class diagram, persistence, GUI design,
  testing strategy, installation, development guide, team contribution template,
  changelog and architecture decision records.
- Utility scripts `scripts/run.sh` and `scripts/verify-environment.sh`.

### Known limitations

- Single-user desktop application; CSV files are not locked against concurrent
  writers.
- Administrator authentication is a built-in demo account, by design.
