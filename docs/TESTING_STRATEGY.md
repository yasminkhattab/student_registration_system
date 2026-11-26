# Testing Strategy

## 1. Goals

- Prove that business rules work without starting JavaFX.
- Prove that CSV persistence survives an application restart.
- Protect the team against regressions while several students edit the code.
- Provide evidence for CLO 2.1 (OOP), 2.3 (tools/testing) and 3.1 (teamwork).

## 2. Test levels

| Level | Tool | Scope | Location |
| --- | --- | --- | --- |
| Unit | JUnit 5 | Validator, CSV helpers, services, domain rules | `src/test/java/.../util`, `.../service` |
| Persistence | JUnit 5 + `@TempDir` | Repository round trips, reload after restart | `.../repository`, `.../service` |
| Manual GUI | Checklist | Screens, navigation, feedback | section 6 of this document |
| Build verification | Maven | Compilation, dependency resolution, packaging | `mvn clean test`, `mvn clean package` |

GUI automation with TestFX is **not** used: adding a UI-testing framework would add a
dependency that is hard to justify for a 15-mark course project, and the controllers
contain almost no logic worth automating. Logic is tested at the service layer and
the UI is verified manually.

## 3. Automated test inventory

| Test class | What it covers |
| --- | --- |
| `ValidatorTest` | ID normalisation, blank/short IDs, e-mail format, phone format, integer ranges |
| `CsvUtilsTest` | Joining, quoting, parsing, doubled quotes, empty fields |
| `FileStorageManagerTest` | Write/read round trip, missing file, overwrite, data directory creation |
| `EntityConstructorsTest` | No-argument constructors and setter population for all entities |
| `RepositoryCsvFormatTest` | CSV column order, quoting, headers, package-private row formatters |
| `StudentServiceTest` | Add, normalise ID, duplicate ID, invalid e-mail, invalid level, update, delete, search, reload from disk |
| `CourseServiceTest` | Add, duplicate ID, credit range, capacity range, update, capacity below enrollment, delete, search, reload from disk |
| `RegistrationServiceTest` | Enroll, duplicate enrollment, full course, unknown student/course, drop, drop without enrollment, reload with rehydration, delete protection for students and courses, listing |

The suite contains **53 tests**. Run them with:

```bash
mvn clean test
```

The Surefire plugin executes every `*Test` class with the JUnit 5 provider. Tests do
not need a display, so they also run on a headless build machine.

## 4. Business-rule traceability

| Rule | Primary test |
| --- | --- |
| Unique student ID | `StudentServiceTest.rejectsDuplicateId` |
| Unique course ID | `CourseServiceTest.rejectsDuplicateId` |
| No duplicate enrollment | `RegistrationServiceTest.rejectsDuplicateEnrollment` |
| Course capacity enforced | `RegistrationServiceTest.rejectsEnrollmentInFullCourse` |
| Student must exist | `RegistrationServiceTest.rejectsUnknownEntities` |
| Course must exist | `RegistrationServiceTest.rejectsUnknownEntities` |
| Enrollment status management | `RegistrationServiceTest.dropsCourse`, `restoresEnrollmentsFromCsv` |
| Capacity cannot drop below enrollment | `CourseServiceTest.rejectsCapacityBelowEnrollment` |
| Deletion protection | `RegistrationServiceTest.blocksDeletionOfEnrolledStudent`, `blocksDeletionOfEnrolledCourse` |
| Persistence across restart | `StudentServiceTest.persistsAcrossInstances`, `RegistrationServiceTest.restoresEnrollmentsFromCsv` |

## 5. Test data strategy

- Every test uses JUnit's `@TempDir`; the real `data/` directory is never touched.
- Services are rebuilt in each test method with `@BeforeEach`, so tests are isolated.
- Persistence is verified by constructing a **new** service instance over the same
  temporary directory, which simulates an application restart.
- No test depends on the order in which it runs.

## 6. Manual GUI test scenarios

Execute from a desktop session with `mvn javafx:run` (or `./scripts/run.sh`).
A graphical session is required; on a headless machine these cases cannot be run.

| ID | Scenario | Steps | Expected result |
| --- | --- | --- | --- |
| GUI-01 | Student login | Role **Student**, ID `STU-1001`, Sign in | Student dashboard opens with Sara's data |
| GUI-02 | Unknown student | Role **Student**, ID `STU-9999` | Inline error “No student found with ID STU-9999.” |
| GUI-03 | Administrator login | Role **Administrator**, `ADM-001`, `admin123` | Administrator dashboard with statistics |
| GUI-04 | Wrong admin password | Role **Administrator**, `ADM-001`, `wrong` | Inline error, no navigation |
| GUI-05 | Add student | Student management → fill valid fields → Add | Row appears, file `data/students.csv` contains it |
| GUI-06 | Duplicate student | Add with an existing ID | Inline “Student ID ... already exists.” |
| GUI-07 | Invalid email | Add with `abc` as email | Inline e-mail validation message |
| GUI-08 | Edit student | Select row, change major/level, Update | Table shows new values; selection cleared |
| GUI-09 | Delete protected student | Select a student enrolled in CS113, Delete | Confirmation, then “Cannot delete ... active enrollment(s)” |
| GUI-10 | Delete free student | Select `STU-1003`, Delete, confirm | Row disappears; CSV updated |
| GUI-11 | Add course | Course management → fill valid fields → Add | Row appears; statistics increase |
| GUI-12 | Invalid capacity | Capacity `0` | Inline range error |
| GUI-13 | Reduce capacity | Select CS113 (2 enrolled), set capacity `1`, Update | Inline error explaining the current enrollment |
| GUI-14 | Inspect enrollments | Select a course | Lower table lists its enrolled students |
| GUI-15 | Drop enrollment (admin) | Select course + student, Drop, confirm | Student removed; enrolled count decreases |
| GUI-16 | Browse and enroll | Student dashboard → Browse courses → select open course → Enroll | Success message; seat count decreases |
| GUI-17 | Duplicate enrollment | Enroll again in the same course | “already enrolled” message |
| GUI-18 | Full course | Fill a course to capacity, try to enroll another student | Enroll button disabled / “course is full” message |
| GUI-19 | Drop course (student) | My courses → select → Drop, confirm | Course disappears; total credits update |
| GUI-20 | Persistence | Enroll, close the app, start again, sign in | Enrollment still listed |
| GUI-21 | Logout | Sign out on any dashboard | Login screen; session user cleared |
| GUI-22 | Window resizing | Resize to minimum 980 × 660 | Layout stays usable, tables scroll |
| GUI-23 | Logo image | Open login and any dashboard | University mark is visible and undistorted on the card/header |

## 7. Acceptance checklist

- [x] `mvn clean test` succeeds with zero failures (53 tests).
- [x] `mvn clean package` produces `target/student-registration-system-1.0.0-SNAPSHOT.jar`.
- [x] The JavaFX window opens on the reference machine (see `docs/INSTALLATION.md`).
- [ ] GUI-01 … GUI-23 pass on the presentation machine.
- [ ] `git status` shows only intended files before the final commit.

## 8. Known testing gaps

- No automated JavaFX UI test (justified above).
- No concurrency tests: the application is single-user by design.
- CSV corruption is detected by `DataAccessException` but not automatically repaired;
  the user must fix or remove the offending row/file.
