# Requirements and Traceability

> **Document status: reconciled with the official brief.**
> Source: [`reference/CS113_Project_Instructions.pdf`](../reference/CS113_Project_Instructions.pdf),
> *Programming II (CS113) – Group Project*, Taibah University, First Semester
> 2026/2027, 15 marks, groups of 4–5 students.
>
> The tables below map each official requirement to the implementation and to the
> evidence a marker can inspect. Team identities, screenshots and the final PDF
> report are group deliverables and are listed in
> [`PROJECT_REPORT_OUTLINE.md`](PROJECT_REPORT_OUTLINE.md).

## 1. Official CLO weighting

| CLO | Assessment area | Marks |
| --- | --- | --- |
| 2.1 | Develop computer programs to solve real-world problems based on object-oriented principles | 5 |
| 2.2 | Implement GUI applications using event-driven programming techniques | 3 |
| 2.3 | Operate current technologies, tools, and libraries for developing OOP applications | 2 |
| 3.1 | Demonstrate effective teamwork through group design, implementation, and presentation | 5 |
| **Total** | | **15** |

### Official rubric breakdown

| CLO | Assessment criterion | Marks | Where this repository provides evidence |
| --- | --- | --- | --- |
| 2.1 | Classes, constructors, attributes, and methods | 2 | Domain model with private state, no-arg + parameterized constructors, accessors/mutators, public/protected/package-private methods |
| 2.1 | OOP principles and class relationships | 2 | Inheritance, polymorphism, abstraction, interface, association, aggregation, composition, overloading/overriding |
| 2.1 | UML diagram and exception handling | 1 | `docs/UML_CLASS_DIAGRAM.md`; exception hierarchy; `IOException` handling in repositories |
| 2.2 | GUI design, components, and navigation | 1 | Seven FXML screens, CSS, images, layout panes, `SceneNavigator` |
| 2.2 | Event handling and interactive functionality | 2 | `@FXML` handlers in seven controllers, validation, feedback, table binding |
| 2.3 | Use of IDE, tools, and libraries | 1 | Maven, JavaFX, JUnit 5, Git; import instructions for IntelliJ IDEA / Eclipse / NetBeans |
| 2.3 | Organization, compilation, and execution | 1 | Layered packages, `mvn clean test`, `mvn clean package`, `mvn javafx:run` |
| 3.1 | Individual contribution and responsibility | 2 | `docs/TEAM_CONTRIBUTION.md` (to be completed with real identities and commits) |
| 3.1 | Team cooperation and coordination | 1 | Contribution log, meeting log, ADRs, changelog |
| 3.1 | Presentation, demonstration, and understanding | 2 | Pre-recorded presentation plan and demonstration script in the report outline |
| **Total** | | **15** | |

## 2. Functional requirements — Administrator

| ID | Requirement | Implementation | Evidence / tests |
| --- | --- | --- | --- |
| FR-ADM-01 | View dashboard statistics (students, courses, active enrollments, seat usage) | `AdminDashboardController`, `StudentService.count()`, `CourseService.count()/getTotalCapacity()/getTotalEnrollment()`, `RegistrationService.countActiveRegistrations()` | `AdminDashboardController.initialize()`, GUI-03 |
| FR-ADM-02 | Add a student | `StudentManagementController.handleAdd()` → `StudentService.addStudent(...)` | `StudentServiceTest.addsStudent` |
| FR-ADM-03 | Edit student information | `handleUpdate()` → `StudentService.updateStudent(...)` | `StudentServiceTest.updatesStudent` |
| FR-ADM-04 | Delete a student record | `handleDelete()` → `RegistrationService.deleteStudent(...)` | `RegistrationServiceTest.blocksDeletionOfEnrolledStudent`, `deletesStudentAfterDroppingCourses` |
| FR-ADM-05 | Search students | `StudentService.search(keyword)`, search bar in `student-management.fxml` | `StudentServiceTest.searchesStudents` |
| FR-ADM-06 | View all available courses | `CourseService.getAllCourses()`, table in `admin-dashboard.fxml` and `course-management.fxml` | GUI-03, GUI-11 |
| FR-ADM-07 | Add and edit courses | `CourseManagementController.handleAdd()/handleUpdate()` → `CourseService.addCourse/updateCourse` | `CourseServiceTest.addsCourse`, `updatesCourse` |
| FR-ADM-08 | Remove courses that have no active enrollments | `RegistrationService.deleteCourse(...)` blocks deletion while `getEnrollmentCount() > 0` | `RegistrationServiceTest.blocksDeletionOfEnrolledCourse` |
| FR-ADM-09 | View course enrollments | Enrollment panel (`enrollmentTable`) → `RegistrationService.getActiveStudentsForCourse(...)` | GUI-14 |
| FR-ADM-10 | Drop an enrollment (administrative correction) | `CourseManagementController.handleDropEnrollment()` → `RegistrationService.drop(...)` | `RegistrationServiceTest.dropsCourse`, GUI-15 |

## 3. Functional requirements — Student

| ID | Requirement | Implementation | Evidence / tests |
| --- | --- | --- | --- |
| FR-STU-01 | Log in with a simple student identifier | `LoginController.signInStudent()` → `Validator.requireText` + `StudentService.findById` | GUI-01, GUI-02 |
| FR-STU-02 | View personal information | `StudentDashboardController` (ID, name, email, phone, major, level) | GUI-01 |
| FR-STU-03 | Browse available courses | `CourseCatalogController` + `CourseService.getAllCourses()` | GUI-16 |
| FR-STU-04 | Search courses | `CourseService.search(keyword)`, search bar in catalogue | `CourseServiceTest.searchesCourses`, GUI-16 |
| FR-STU-05 | Enroll in a course | `CourseCatalogController.handleEnroll()` → `RegistrationService.enroll(...)` | `RegistrationServiceTest.enrollsStudent` |
| FR-STU-06 | View registered courses | `MyCoursesController`, `StudentDashboardController` → `getActiveCoursesForStudent(...)` | GUI-19, GUI-20 |
| FR-STU-07 | Drop a course when permitted | `MyCoursesController.handleDrop()` → `RegistrationService.drop(...)` | `RegistrationServiceTest.dropsCourse` |
| FR-STU-08 | Sign out and return to login | `context.logout()` + `SceneNavigator.showLogin()` | GUI-21 |

## 4. Business rules

All rules are implemented outside JavaFX controllers.

| ID | Rule | Implementation | Test |
| --- | --- | --- | --- |
| BR-01 | Student IDs are unique | `StudentService.addStudent` checks `findOptional(studentId)` before insertion | `StudentServiceTest.rejectsDuplicateId` |
| BR-02 | Course IDs are unique | `CourseService.addCourse` checks the catalogue | `CourseServiceTest.rejectsDuplicateId` |
| BR-03 | A student cannot enroll twice in the same course | `RegistrationService.enroll` (active record check) and `Course.isEnrolled` | `RegistrationServiceTest.rejectsDuplicateEnrollment` |
| BR-04 | Enrollment respects course capacity | `Course.enroll` → `isFull()` → `CourseFullException` | `RegistrationServiceTest.rejectsEnrollmentInFullCourse` |
| BR-05 | Enrollment in a full course is refused | Same as BR-04; button disabled when full | `RegistrationServiceTest.rejectsEnrollmentInFullCourse`, GUI-18 |
| BR-06 | The student must exist | `RegistrationService.enroll` → `StudentService.findById` | `RegistrationServiceTest.rejectsUnknownEntities` |
| BR-07 | The course must exist | `RegistrationService.enroll` → `CourseService.findById` | `RegistrationServiceTest.rejectsUnknownEntities` |
| BR-08 | Enrollment status is managed | `RegistrationStatus.ACTIVE/DROPPED`; drop keeps history | `RegistrationServiceTest.dropsCourse`, `restoresEnrollmentsFromCsv` |
| BR-09 | Errors produce meaningful messages | `ValidationException`, `DuplicateEnrollmentException`, `CourseFullException`, `EntityNotFoundException`; inline status labels | GUI-02/06/07/09/12/13/17/18 |
| BR-10 | Students/courses with active enrollments cannot be deleted | `RegistrationService.deleteStudent/deleteCourse` | `blocksDeletionOfEnrolledStudent`, `blocksDeletionOfEnrolledCourse` |
| BR-11 | Capacity cannot fall below current enrollment | `CourseService.updateCourse` + `Course.setCapacity` | `CourseServiceTest.rejectsCapacityBelowEnrollment` |

## 5. Technical requirements

| ID | Requirement | Implementation | Verification |
| --- | --- | --- | --- |
| TR-01 | Java 21 LTS | `maven.compiler.release=21`; Corretto 21.0.13.1 installed | `mvn -version` reports Java 21; build succeeds |
| TR-02 | JavaFX declared and resolved as a library | `pom.xml`: `javafx-controls`, `javafx-fxml`, `javafx-graphics` 21.0.13 | `mvn clean package` resolves them from Maven Central |
| TR-03 | JavaFX Controls, FXML and Graphics | Seven FXML layouts, controls in every screen, `Scene`/`Stage` graphics | All screens loaded during GUI verification |
| TR-04 | FXML for layouts | `src/main/resources/fxml/*.fxml` | Loaded by `SceneNavigator` |
| TR-05 | CSS for styling | `src/main/resources/css/styles.css` applied to every scene | Screens rendered with the shared theme |
| TR-06 | Maven build and run | `maven-compiler-plugin`, `maven-surefire-plugin`, `javafx-maven-plugin` | `mvn clean package`, `mvn javafx:run` |
| TR-07 | JUnit 5 testing | **53 tests** across validator, CSV, storage, constructors, repositories and services | `mvn clean test` — 0 failures |
| TR-08 | CSV persistence with `IOException` handling | `FileStorageManager` (`java.nio.file`), repositories wrap `IOException` in `DataAccessException` | `FileStorageManagerTest`, `persistsAcrossInstances`, `restoresEnrollmentsFromCsv` |
| TR-09 | At least 5 Java types: 1 interface, 1 abstract, 3 concrete | `Registrable`; abstract `Person`; `Student`, `Administrator`, `Course`, `Registration` | Source tree |
| TR-10 | At least two functional GUI windows/scenes | Seven scenes: login, administrator dashboard, student management, course management, student dashboard, catalogue, my courses | GUI verification |
| TR-11 | Navigation and event handlers | `SceneNavigator`, `@FXML` `onAction` handlers in each controller | GUI-01 … GUI-21 |
| TR-12 | Input validation and meaningful feedback | `Validator`; inline `status-label` messages; confirmation dialogs | GUI-06, GUI-07, GUI-12, GUI-13 |
| TR-13 | Business rules outside controllers | Rules in `Course`, `RegistrationService`; controllers delegate | `docs/ARCHITECTURE.md` §3; test suite |
| TR-14 | Encapsulation, inheritance, polymorphism, abstraction, interface, association, aggregation, overloading, overriding, constructors, accessors/mutators | See mapping table in §6 | UML + source |
| TR-15 | Avoid enterprise frameworks | No Spring/Hibernate/REST/Docker; plain Maven project | `pom.xml` |
| TR-16 | JavaDoc on important public API | JavaDoc on model, services, repositories, config, utils | Source |
| TR-17 | UML model with attributes, constructors, methods, relationships | Mermaid class diagrams matching the code | `docs/UML_CLASS_DIAGRAM.md` |
| TR-18 | Git repository with professional ignore rules | `git init -b main`, `.gitignore`, initial commit | `git log`, `git status` |
| TR-19 | No remote repository configured | No `git remote` added | `git remote -v` is empty |
| TR-20 | No-argument **and** parameterized constructors | `Person()` (protected); public no-arg constructors in `Student`, `Administrator`, `Course`, `Registration`, plus their parameterized forms | `EntityConstructorsTest` |
| TR-21 | Images in the GUI | `university-mark.png` displayed through `ImageView` on the login card and every screen header (FXML `<Image url="@../images/university-mark.png"/>`) | GUI verification; all seven screens load |
| TR-22 | Approved Java IDE | Project is a standard Maven project; `docs/INSTALLATION.md` §7 gives exact steps for IntelliJ IDEA, Eclipse and NetBeans | Group must demonstrate one of these IDE builds before submission — the reference machine only had VS Code/Antigravity installed |
| TR-23 | Report and source submission | `docs/PROJECT_REPORT_OUTLINE.md`; `scripts/package-submission.sh` produces `CS113_<Section>_Project_Group<X>.zip` | Run the script; export the report to PDF |

## 6. Object-oriented concept mapping (CLO 2.1)

| Concept | Implementation |
| --- | --- |
| Encapsulation | Private fields and controlled access in `Person`, `Course`, `Registration` |
| Abstraction | Abstract `Person`; `Registrable` interface |
| Inheritance | `Student extends Person`; `Administrator extends Person` |
| Polymorphism | `Person` references dispatch to role-specific `getRole()` / `getContactSummary()` |
| Interface implementation | `Course implements Registrable` |
| Association | `Registration` refers to `studentId` and `courseId`; services reference repositories |
| Aggregation | `Course.enrolledStudents : List<Student>` |
| Composition | `AppContext` owns services; `RegistrationService` owns registration records |
| Method overloading | Two `Student` constructors; two `StudentService.addStudent` methods |
| Method overriding | `getRole`, `getContactSummary`, `enroll`, `drop`, `isEnrolled`, `isFull`, accessors |
| No-arg constructors | `Person()` (protected), plus public no-arg constructors in `Student`, `Administrator`, `Course`, `Registration`; populated through setters for demonstration |
| Parameterized constructors | All entities, services, repositories and `AppContext` are constructed with arguments |
| Accessors / mutators | Getters and setters on entities, with validation in setters |
| Access modifiers | `private` state; `protected` `Person` constructor/helper; package-private `toCsvLine(...)` in the three repositories (visible to tests in the same package); public API elsewhere |
| Exception handling | `RegistrationException` hierarchy + `DataAccessException` wrapping `IOException` |

## 7. CLO mapping

| CLO | Official description | Where it is demonstrated | Marks |
| --- | --- | --- | --- |
| **CLO 2.1** | Develop OOP programs for real-world problems | Domain model, interface, abstract class, service layer, exception hierarchy (TR-09, TR-14, TR-17, TR-20) | 5 |
| **CLO 2.2** | Implement GUI applications with event-driven programming | Seven FXML screens, images, controllers, navigation, validation feedback, CSS (FR-ADM-01, FR-STU-01…08, TR-03/04/05/10/11/12/21) | 3 |
| **CLO 2.3** | Operate current technologies, tools and libraries | Maven build, JavaFX, JUnit 5, Git, CSV file APIs, IDE import instructions (TR-01/02/06/07/08/18/22) | 2 |
| **CLO 3.1** | Demonstrate effective teamwork through design, implementation and presentation | Contribution log, meeting log, ADRs, changelog, architecture documentation, reproducible setup, pre-recorded presentation | 5 |

### Coverage summary

| CLO | Strong evidence in this repository | Remaining group action |
| --- | --- | --- |
| 2.1 | Interface, abstract class, four concrete classes, no-arg and parameterized constructors, package-private methods, tests | Insert the exported UML image in the report PDF |
| 2.2 | Seven functional screens with real workflows and images | Capture final screenshots; dry-run the demonstration |
| 2.3 | Build, tests, Git, file persistence, IDE instructions | Open the project once in IntelliJ IDEA, Eclipse or NetBeans and show it during the presentation |
| 3.1 | Templates, ADRs, changelog, verification record | Fill real names, IDs, commits and contribution evidence; record the presentation |

## 8. Non-functional requirements

| ID | Requirement | How it is met |
| --- | --- | --- |
| NFR-01 | Usable on a standard laptop | Plain JavaFX, no external services, < 100 MB heap in normal use |
| NFR-02 | Understandable code for second-year students | No generated code, constructor injection, simple CSV layer, JavaDoc |
| NFR-03 | Reproducible build | Maven with pinned versions; `docs/INSTALLATION.md` |
| NFR-04 | Data integrity on save | Temporary file + atomic move; rollback of in-memory state on failure |
| NFR-05 | Clear error messages | Domain exceptions with user-facing text; inline feedback |
| NFR-06 | Maintainability | Layered packages, tests, ADRs, changelog, development guide |
| NFR-07 | Portability | Pure Java + JavaFX; CSV path relative to working directory |

## 9. Official brief compliance checklist

| Official requirement | Status | Evidence |
| --- | --- | --- |
| Groups of 4–5 students | Group action | `docs/TEAM_CONTRIBUTION.md` |
| At least 5 Java types (1 interface, 1 abstract, ≥3 concrete) | Done | `Registrable`; `Person`; `Student`, `Administrator`, `Course`, `Registration` |
| Private attributes | Done | All entity fields |
| No-argument and parameterized constructors | Done | `EntityConstructorsTest`; UML §1 |
| Accessor and mutator methods | Done | Entity getters/setters |
| Encapsulation, inheritance, polymorphism, abstraction, interface implementation | Done | `docs/UML_CLASS_DIAGRAM.md` §3 |
| Public, protected and package-private methods | Done | `Person` protected constructor; `toCsvLine(...)` package-private in repositories |
| Association, aggregation/composition, inheritance | Done | `Course` aggregates `Student`; `AppContext` composes services |
| Method overloading and overriding | Done | Constructors, `addStudent`, `getRole`, `enroll` |
| Exception handling incl. `IOException` for file operations | Done | `exception/` package; `FileStorageManager` |
| UML class diagram with attributes, constructors, methods, relationships | Done | `docs/UML_CLASS_DIAGRAM.md` |
| At least two functional GUI windows/scenes | Done (seven) | `src/main/resources/fxml/` |
| Buttons, labels, text fields, images, layout panes | Done | FXML + `university-mark.png` |
| Event handlers and navigation between scenes | Done | Controllers + `SceneNavigator` |
| Input validation, meaningful output, user feedback | Done | `Validator`; inline status labels |
| Suitable IDE (IntelliJ IDEA, Eclipse or NetBeans) | **Group action** | `docs/INSTALLATION.md` §7; must be demonstrated |
| JavaFX GUI library | Done | `pom.xml` |
| Packages/classes organisation | Done | Layered packages |
| Compiles and runs successfully | Done | `mvn clean test`, `mvn clean package`, GUI launch |
| Report PDF with cover page and Sections 1–4 | **Group action** | `docs/PROJECT_REPORT_OUTLINE.md` |
| Source ZIP named `CS113_Section_Project_GroupX` | **Group action** | `scripts/package-submission.sh` |
| Pre-recorded presentation, each member presents their part | **Group action** | Outline §5 |
| Deadline 12 December 2026, 11:59 PM | Group action | Submit via Blackboard |
| Plagiarism rules respected | Group action | All code in this repository was written for the project; cite any external source |

## 10. Verification record

| Check | Date | Result |
| --- | --- | --- |
| `mvn clean test` | 2026-11-24 | 53 tests, 0 failures, 0 errors |
| `mvn clean package` | 2026-11-24 | BUILD SUCCESS; JAR produced |
| JavaFX launch | 2026-11-24 | Login window opened with the expected title |
| All seven screens load | 2026-11-24 | Verified by rendering each screen and capturing snapshots, including the logo image |
| Git repository | 2026-11-24 | `main` branch initialised, no remote configured |
