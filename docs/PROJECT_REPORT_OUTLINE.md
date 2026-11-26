# Project Report Outline and Submission Checklist

> **Template for the group.** Replace every `_placeholder_` with real information.
> Nothing in this file invents student names, IDs or contributions.

Official source: [`reference/CS113_Project_Instructions.pdf`](../reference/CS113_Project_Instructions.pdf).

## 1. Cover page (required)

| Field | Value |
| --- | --- |
| University | Taibah University |
| College | College of Computer Science and Engineering |
| Department | Department of Computer Science |
| Course title | Programming II – CS113 |
| Semester | First Semester 2026/2027 |
| Section | _e.g. CB14_ |
| Group number | _e.g. Group A_ |
| Project title | University Student Course Registration System |
| Submission date | _to be filled_ |

| # | Student name | Student ID | Main responsibility |
| --- | --- | --- | --- |
| 1 | _to be filled_ | _to be filled_ | _to be filled_ |
| 2 | _to be filled_ | _to be filled_ | _to be filled_ |
| 3 | _to be filled_ | _to be filled_ | _to be filled_ |
| 4 | _to be filled_ | _to be filled_ | _to be filled_ |
| 5 (optional) | _to be filled_ | _to be filled_ | _to be filled_ |

The brief requires the course title, section, group number, student names and
student IDs on the cover page.

## 2. Section 1 — Introduction

### 2.1 Problem

Manual course registration is slow and error-prone: students queue for signatures,
staff copy identifiers between sheets, the same student is sometimes registered twice,
and a course can be over-filled because nobody checks capacity at the moment of
registration.

### 2.2 Objectives

1. Replace the paper workflow with a desktop application for administrators and
   students.
2. Enforce registration rules automatically: unique IDs, no duplicate enrollment,
   capacity limits and valid references.
3. Persist data between sessions without a database server.
4. Demonstrate the CS113 outcomes: OOP, JavaFX event handling, tools/libraries and
   teamwork.

### 2.3 Proposed solution

A Java 21 / JavaFX application with two roles:

- **Administrator** — manages students and courses, inspects enrollments and can drop
  a registration.
- **Student** — signs in with a student ID, browses the catalogue, enrolls and drops
  courses.

Data is stored in three CSV files (`students.csv`, `courses.csv`,
`registrations.csv`) written through `java.nio.file` with atomic replacement.
Business rules live in services and the domain model, not in controllers.

Add one short paragraph naming the team's chosen scope and any assumptions (for
example: single administrator account, no tuition/payment processing, no timetable
conflict detection).

## 3. Section 2 — System Design

### 3.1 UML class diagram

1. Open [`UML_CLASS_DIAGRAM.md`](UML_CLASS_DIAGRAM.md).
2. Export the two Mermaid diagrams as PNG/SVG (Mermaid Live Editor, the IDE Mermaid
   plugin, or `mermaid-cli`).
3. Insert them in the report with a caption and a short explanation of the symbols.

The report must show attributes, constructors, methods and relationships. The
diagrams already include:

- `Person` (abstract) with `Student` and `Administrator` subclasses.
- `Registrable` interface implemented by `Course`.
- `Registration` associated with a student and a course by identifier.
- `Course` aggregating `List<Student>`.
- `AppContext` composing the three services.
- No-argument and parameterized constructors.
- Package-private `toCsvLine(...)` methods in the repositories.

### 3.2 Class relationships to explain

| Relationship | Class pair | Why it matters |
| --- | --- | --- |
| Inheritance | `Student` / `Administrator` → `Person` | Shared identity fields and polymorphic role behaviour |
| Interface realisation | `Course` → `Registrable` | The catalogue treats any registrable offering uniformly |
| Aggregation | `Course` → `Student` | Students exist independently of a course |
| Association | `Registration` → `Student`, `Course` | A registration records which student joined which course |
| Composition | `AppContext` → services | The application owns the service lifetime |
| Dependency | Controllers → services | The GUI delegates all rules |

### 3.3 Design decisions

Summarise two or three ADRs, for example:

- CSV files instead of a database (ADR 0001).
- JavaFX resolved by Maven, non-modular project (ADR 0002, 0003).
- Business rules outside controllers (ADR 0004).
- Single built-in administrator, documented as demo-only (ADR 0005).

## 4. Section 3 — Implementation

### 4.1 Package organisation

| Package | Contents |
| --- | --- |
| `model` | `Person`, `Student`, `Administrator`, `Course`, `Registration`, `RegistrationStatus` |
| `interfaces` | `Registrable` |
| `service` | `StudentService`, `CourseService`, `RegistrationService` |
| `repository` | Three CSV repositories and `FileStorageManager` |
| `exception` | Validation, full-course, duplicate, not-found, data-access types |
| `controller` | Seven FXML controllers |
| `util` | `Validator`, `CsvUtils`, `AlertHelper`, `SceneNavigator` |
| `config` | `AppConfig`, `AppContext`, `SampleDataInitializer` |

### 4.2 OOP evidence

| Concept | File / member to show |
| --- | --- |
| Encapsulation | Private fields and validated setters in `Person`, `Course` |
| Abstraction | `Person` declared `abstract`; `Registrable` interface |
| Inheritance | `Student extends Person`, `Administrator extends Person` |
| Polymorphism | `Person.getRole()` / `getContactSummary()` |
| Overloading | `Student(...)` constructors; `StudentService.addStudent(...)` |
| Overriding | `getRole()`, `enroll()`, `isFull()`, accessors |
| No-arg constructors | `new Student()` then setters (see `EntityConstructorsTest`) |
| Package-private | `StudentRepository.toCsvLine(...)` |
| Exception handling | `CourseFullException`, `DuplicateEnrollmentException`; `IOException` → `DataAccessException` |
| File I/O | `FileStorageManager` write-to-temp + atomic move |

### 4.3 GUI evidence

Insert screenshots of:

1. Login screen (shows the university mark image).
2. Administrator dashboard.
3. Student management with a validation message.
4. Course management with the enrollment panel.
5. Student dashboard.
6. Course catalogue.
7. My registered courses.
8. A confirmation dialog (drop course) and a success message.

Explain the FXML/controller/CSS split and the `SceneNavigator` flow. Mention that
images are declared with the nested `<Image>` element because JavaFX does not coerce
an image path string.

### 4.4 Code excerpts

Include short excerpts only (5–15 lines each) for:

- `Person` fields and both constructors.
- `Course.enroll(...)` capacity check.
- `RegistrationService.enroll(...)` duplicate/existence checks.
- `FileStorageManager.writeLines(...)` atomic write.
- One `@FXML` handler showing validation and feedback.

## 5. Section 4 — Testing and Results

### 5.1 Automated tests

Paste the Surefire summary from `mvn clean test`:

```text
Tests run: 53, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Explain the layers: validator/CSV tests, repository round trips with `@TempDir`,
service rule tests, constructor and CSV-format tests.

### 5.2 Manual GUI results

Reproduce the table from
[`TESTING_STRATEGY.md`](TESTING_STRATEGY.md#6-manual-gui-test-scenarios) with a
**Pass/Fail** column filled by the group. Include screenshots of the most important
cases:

- Unknown student ID (inline error).
- Duplicate student ID.
- Course full / enroll button disabled.
- Protected deletion.
- Persistence after restart.

### 5.3 Results and limitations

State what works and what is deliberately out of scope:

- Single-user desktop app; no concurrent writers.
- Built-in demo administrator, not production authentication.
- No automated JavaFX UI tests (logic is tested at the service layer).
- CSV corruption must be fixed manually.

## 6. Presentation plan (pre-recorded)

The brief requires a pre-recorded presentation in which each member presents their
assigned part. Suggested 10–12 minute structure:

| Time | Segment | Presenter |
| --- | --- | --- |
| 0:00–1:30 | Problem, objectives, demo overview | _Member 1_ |
| 1:30–4:00 | UML, class relationships, OOP concepts | _Member 2_ |
| 4:00–6:30 | Live demo: administrator flows | _Member 3_ |
| 6:30–8:30 | Live demo: student flows and validation | _Member 4_ |
| 8:30–10:00 | Testing evidence, tools, IDE and build | _Member 5 / shared_ |
| 10:00–11:30 | Teamwork, lessons learned, questions | _All_ |

Every member must be able to answer questions about the code they present. Record
the screen and narration, keep the video length within the instructor's limit, and
name the file using the group identifier.

## 7. Submission checklist

- [ ] Cover page complete (course, section, group, names, IDs).
- [ ] Section 1 Introduction written.
- [ ] Section 2 System Design includes exported UML and relationship explanations.
- [ ] Section 3 Implementation includes organised code excerpts and GUI screenshots.
- [ ] Section 4 Testing and Results includes the test summary and manual results.
- [ ] Report exported as **PDF**.
- [ ] Source archive created with `./scripts/package-submission.sh <section> <group>`.
- [ ] ZIP named `CS113_<Section>_Project_Group<X>.zip` (for example
      `CS113_CB14_Project_GroupA.zip`).
- [ ] ZIP extracted into a clean directory and verified with `mvn clean test` and
      `mvn javafx:run`.
- [ ] Project opened and built once in IntelliJ IDEA, Eclipse or NetBeans.
- [ ] `docs/TEAM_CONTRIBUTION.md` filled with real evidence (commits, files, tests).
- [ ] Presentation recorded; each member presents their part.
- [ ] Both files uploaded to Blackboard before **12 December 2026, 11:59 PM**.
- [ ] No plagiarism; all external sources cited.

## 8. Academic integrity

The code in this repository was written for this project. Do not copy another
group's report, code or screenshots. Cite any library documentation or tutorial that
influenced the implementation. Plagiarism can result in a zero mark under the
university's academic integrity regulations.
