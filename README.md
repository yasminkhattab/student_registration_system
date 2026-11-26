# University Student Course Registration System

Desktop application for the **Programming II (CS113)** group project at
**Taibah University**, First Semester 2026/2027.

Administrators manage students and courses; students sign in with their student ID,
browse the catalogue and manage their registrations. The project demonstrates
object-oriented design, JavaFX event handling, exception handling and file-based
persistence without enterprise frameworks.

## Features

### Administrator

- Dashboard with student, course, enrollment and seat-usage statistics.
- Add, edit, delete and search students.
- Add, edit and delete courses (deletion is blocked while students are enrolled).
- Inspect the enrolled students of any course and drop an enrollment.

### Student

- Sign in with a student identifier.
- View personal information and current registrations.
- Browse and search courses with live seat availability.
- Enroll in a course and drop a course.

### Business rules enforced in the service layer

- Unique student and course identifiers.
- No duplicate enrollment in the same course.
- Enrollment blocked when a course is full.
- Student and course existence validated before enrollment.
- Capacity cannot be reduced below the current enrollment.
- Students/courses with active enrollments cannot be deleted.
- Dropped registrations are kept with status `DROPPED`.

## Technology stack

| Layer | Technology |
| --- | --- |
| Language | Java 21 LTS |
| GUI | JavaFX 21.0.13 (Controls, FXML, Graphics) + CSS |
| Build | Apache Maven 3.9 |
| Persistence | CSV files via `java.nio.file` and `IOException` handling |
| Testing | JUnit 5 (53 tests) |
| Version control | Git |

No Spring, Hibernate, database server, REST API or Docker is used — by design.

## Official brief at a glance

| Item | Value |
| --- | --- |
| Course | Programming II (CS113), Taibah University |
| Semester | First Semester 2026/2027 |
| Project weight | 15 marks (CLO 2.1 = 5, 2.2 = 3, 2.3 = 2, 3.1 = 5) |
| Group size | 4–5 students |
| Submission | Report PDF + source ZIP via Blackboard |
| ZIP name | `CS113_Section_Project_GroupX` |
| Deadline | 12 December 2026, 11:59 PM |
| Presentation | Pre-recorded; every member presents their assigned part |

The official instructions are stored in
[`reference/CS113_Project_Instructions.pdf`](reference/CS113_Project_Instructions.pdf).
The full traceability matrix is in
[`docs/REQUIREMENTS.md`](docs/REQUIREMENTS.md).

## Prerequisites

- **JDK 21** with `javac` available (`java -version`, `javac -version`).
- **Apache Maven 3.9+** (`mvn -version`).
- **Git** (only needed to clone/commit).
- A graphical desktop session. JavaFX needs GTK 3 on Linux.
- One of the approved IDEs — **IntelliJ IDEA, Eclipse or NetBeans** — for the
  CLO 2.3 demonstration. The project imports as a standard Maven project.

Detailed setup, including the exact toolchain installed on the reference machine,
is in [`docs/INSTALLATION.md`](docs/INSTALLATION.md).

## Installation

```bash
# 1. Clone or copy the repository
cd student-registration-system

# 2. Check the toolchain
java -version      # must report 21.x
javac -version
mvn -version       # must report Java 21
git --version

# 3. Resolve dependencies, run the tests and build
mvn clean test
mvn clean package
```

Maven downloads JavaFX and JUnit 5 from Maven Central on the first build.

## Run the application

```bash
cd student-registration-system
mvn clean javafx:run
# or
./scripts/run.sh
```

The window opens on the login screen. Data is stored in `./data` and seeded with
demo records on the first run.

### Demo accounts

| Role | Identifier | Password |
| --- | --- | --- |
| Administrator | `ADM-001` | `admin123` (override with `SRS_ADMIN_PASSWORD`) |
| Student | `STU-1001`, `STU-1002`, `STU-1003` | — |

> The built-in administrator account is a course-project simplification, not
> production authentication.

The project requires **JDK 21** and Maven 3.9 or newer. JavaFX 21.0.13 and the
JavaFX Maven Plugin 0.0.8 are declared in `pom.xml`; the plugin launches
`com.studentregistration.Main`. Maven resolves the JavaFX modules for the current
operating system. This is a desktop application, so the command must run in a
session that can open a graphical display. See
[`docs/INSTALLATION.md`](docs/INSTALLATION.md#9-troubleshooting) if Maven reports a
plugin-resolution error or JavaFX cannot initialize.

## Project structure

```
student-registration-system/
├── pom.xml                     Maven build and JavaFX configuration
├── README.md
├── LICENSE
├── .gitignore
├── reference/                  Official CS113 brief (PDF)
├── docs/                       Architecture, requirements, design and guides
│   └── decisions/              Architecture decision records
├── src/
│   ├── main/
│   │   ├── java/com/studentregistration/
│   │   │   ├── Main.java       JavaFX entry point
│   │   │   ├── config/         AppContext, constants, demo data
│   │   │   ├── model/          Person, Student, Administrator, Course, Registration
│   │   │   ├── interfaces/     Registrable
│   │   │   ├── service/        Student, course and registration services
│   │   │   ├── repository/     CSV repositories and FileStorageManager
│   │   │   ├── exception/      Business and persistence exceptions
│   │   │   ├── controller/     Seven FXML controllers
│   │   │   └── util/           Validation, CSV, alerts, navigation
│   │   └── resources/
│   │       ├── fxml/           Seven screen layouts
│   │       ├── css/            Shared stylesheet
│   │       └── images/         University mark shown on every screen
│   └── test/java/com/studentregistration/
│       ├── util/               Validator and CSV tests
│       ├── repository/         File storage tests
│       └── service/            Business rule and persistence tests
├── data/                       Runtime CSV files (ignored by Git, .gitkeep kept)
└── scripts/                    run, environment check and submission packaging
```

## How the design fits the course

| Requirement | Implementation |
| --- | --- |
| At least 5 Java types, 1 interface, 1 abstract class, 3 concrete classes | `Registrable`; abstract `Person`; `Student`, `Administrator`, `Course`, `Registration` |
| Inheritance and polymorphism | `Person` subclasses; overridden `getRole()` / `getContactSummary()` |
| Encapsulation, constructors, accessors/mutators | Private fields; no-argument **and** parameterized constructors; validated setters |
| Public, protected and package-private methods | Protected `Person` constructor; package-private `toCsvLine(...)` in repositories; public services and controllers |
| Method overloading | Two `Student` constructors; two `StudentService.addStudent(...)` methods |
| Association and aggregation | `Course` aggregates `List<Student>`; `Registration` references IDs |
| Event-driven GUI with images | Seven FXML screens, `ImageView` logo, `@FXML` handlers |
| Exception handling | `RegistrationException` hierarchy; `IOException` wrapped as `DataAccessException` |
| File operations | Repositories + atomic writes in `FileStorageManager` |
| Testing | 53 JUnit 5 tests |
| UML | Mermaid class diagram in [`docs/UML_CLASS_DIAGRAM.md`](docs/UML_CLASS_DIAGRAM.md) |

## Tests

```bash
mvn clean test
# 53 tests, 0 failures (verified on the reference environment)
```

Test classes: `ValidatorTest`, `CsvUtilsTest`, `FileStorageManagerTest`,
`StudentServiceTest`, `CourseServiceTest`, `RegistrationServiceTest`,
`EntityConstructorsTest`, `RepositoryCsvFormatTest`.

Manual GUI scenarios are listed in
[`docs/TESTING_STRATEGY.md`](docs/TESTING_STRATEGY.md#6-manual-gui-test-scenarios).

## Submission

1. Fill [`docs/TEAM_CONTRIBUTION.md`](docs/TEAM_CONTRIBUTION.md) and the cover page
   in [`docs/PROJECT_REPORT_OUTLINE.md`](docs/PROJECT_REPORT_OUTLINE.md).
2. Export the report to **PDF** with Sections 1–4 and real screenshots.
3. Build the source archive:

   ```bash
   ./scripts/package-submission.sh CB14 A
   # produces CS113_CB14_Project_GroupA.zip
   ```

4. Submit both files through Blackboard before **12 December 2026, 11:59 PM**.
5. Record the presentation; each member explains their own components.

## Documentation

| Document | What it covers |
| --- | --- |
| [`docs/PROJECT_OVERVIEW.md`](docs/PROJECT_OVERVIEW.md) | Scope, features and learning outcomes |
| [`docs/REQUIREMENTS.md`](docs/REQUIREMENTS.md) | Official requirements, rubric and traceability to code |
| [`docs/PROJECT_REPORT_OUTLINE.md`](docs/PROJECT_REPORT_OUTLINE.md) | Report cover page, Sections 1–4 and presentation plan |
| [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) | Layers, packages, data flow and error handling |
| [`docs/UML_CLASS_DIAGRAM.md`](docs/UML_CLASS_DIAGRAM.md) | Mermaid UML matching the code |
| [`docs/DATABASE_AND_PERSISTENCE.md`](docs/DATABASE_AND_PERSISTENCE.md) | CSV schema, write safety and limitations |
| [`docs/GUI_DESIGN.md`](docs/GUI_DESIGN.md) | Screens, navigation and visual conventions |
| [`docs/TESTING_STRATEGY.md`](docs/TESTING_STRATEGY.md) | Automated tests and manual scenarios |
| [`docs/INSTALLATION.md`](docs/INSTALLATION.md) | Toolchain setup, IDE import and troubleshooting |
| [`docs/DEVELOPMENT_GUIDE.md`](docs/DEVELOPMENT_GUIDE.md) | Conventions, checklists and Git workflow |
| [`docs/TEAM_CONTRIBUTION.md`](docs/TEAM_CONTRIBUTION.md) | Contribution template for 4–5 students |
| [`docs/CHANGELOG.md`](docs/CHANGELOG.md) | Version history |

## Team

The repository is prepared for a group of four or five students. Fill the real
identities and contribution evidence in
[`docs/TEAM_CONTRIBUTION.md`](docs/TEAM_CONTRIBUTION.md).

| Member | Student ID | Main responsibility |
| --- | --- | --- |
| _to be filled_ | _to be filled_ | _e.g. domain model and services_ |
| _to be filled_ | _to be filled_ | _e.g. JavaFX screens and styling_ |
| _to be filled_ | _to be filled_ | _e.g. persistence and testing_ |
| _to be filled_ | _to be filled_ | _e.g. documentation and integration_ |
| _to be filled (optional)_ | _to be filled_ | _e.g. manual testing and demonstration_ |

## Contributing

Read [`CONTRIBUTING.md`](CONTRIBUTING.md) for the branch and pull request workflow.
All changes should be made on a new branch from `develop` and submitted as a pull
request into `develop`. Do not commit directly to `main` or `develop`.

The repository includes GitHub issue forms, a pull request template and a Maven CI
workflow. See [`docs/DEVELOPMENT_GUIDE.md`](docs/DEVELOPMENT_GUIDE.md) for coding
conventions and project structure.

The repository has **no remote configured**; the group decides whether to add one.

## License

Released under the MIT License — see [`LICENSE`](LICENSE). The licence covers the
student project code; course material remains the property of its author.
