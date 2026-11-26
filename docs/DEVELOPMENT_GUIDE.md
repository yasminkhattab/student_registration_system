# Development Guide

## 1. Before you start

Read, in this order:

1. [`README.md`](../README.md) — how to build and run.
2. [`ARCHITECTURE.md`](ARCHITECTURE.md) — where code belongs.
3. [`UML_CLASS_DIAGRAM.md`](UML_CLASS_DIAGRAM.md) — the current design.
4. [`DEVELOPMENT_GUIDE.md`](DEVELOPMENT_GUIDE.md) — this document.

## 2. Build lifecycle

| Command | Purpose |
| --- | --- |
| `mvn clean test` | Compile main and test code, run JUnit 5 |
| `mvn clean package` | Run tests, build the JAR in `target/` |
| `mvn javafx:run` | Launch the desktop application |
| `mvn -q javafx:run` | Launch with less Maven output |
| `./scripts/run.sh` | Launch with toolchain detection |
| `./scripts/verify-environment.sh` | Print Java, Maven, Git and display status |
| `./scripts/package-submission.sh <section> <group>` | Build the `CS113_..._Project_Group...zip` source archive from `HEAD` |

Maven downloads dependencies into `~/.m2/repository`; the first build takes longer.

## 3. Where code belongs

| You are adding… | Package | Rules |
| --- | --- | --- |
| A new entity or value object | `model` | Private fields, accessors, no JavaFX imports |
| A contract between components | `interfaces` | Small and behaviour-oriented |
| A business rule or use case | `service` | No JavaFX imports, throw `RegistrationException` subclasses |
| File read/write | `repository` | Wrap `IOException` in `DataAccessException` |
| A screen | `controller` + `resources/fxml` | Event handling and binding only |
| A reusable helper | `util` | Stateless, no domain knowledge |
| A new error type | `exception` | Extend the closest existing exception |

**Never** put a business decision in a controller. If a rule must hold for both the
GUI and future clients, it belongs in a service or in the model.

## 4. Coding conventions

- Java 21, four-space indentation, one public top-level type per file.
- `PascalCase` for types, `camelCase` for methods and fields, `UPPER_SNAKE_CASE` for
  constants.
- Fields are `private`; expose behaviour before getters.
- Prefer `List.copyOf` / unmodifiable views when returning internal collections.
- Use JavaDoc on every public class and public method that is not obvious. Mention
  exceptions that callers must handle.
- Keep methods short; extract a private helper instead of adding comments that
  explain nested logic.
- Use `Optional` for lookups that may legitimately fail, and exceptions for
  operations that must succeed.
- Never catch an exception and ignore it. Either handle it, translate it, or let it
  propagate.
- No `System.out.println` in application code; user messages go through the GUI.

## 5. Adding a feature — checklists

### 5.1 New screen

1. Create `src/main/resources/fxml/my-screen.fxml` with `fx:controller` set.
2. Create `controller/MyScreenController` with a **public constructor accepting
   `AppContext`**, an `@FXML initialize()` and `@FXML` handlers.
3. Add a `showMyScreen()` method to `SceneNavigator` if the screen is reachable.
4. Add navigation buttons in the FXML of the source screen.
5. Use existing style classes (`card`, `btn-primary`, `field-label`, `status-label`);
   add new styles to `styles.css` only when necessary.
6. Run the app, test the screen, then add a row to `docs/GUI_DESIGN.md`.

### 5.2 New business rule

1. Decide whether the rule concerns one object (model) or several objects (service).
2. Implement it in a service/model method, not in a controller.
3. Throw a `RegistrationException` subclass with a message a student can understand.
4. Add a JUnit test in the matching `*ServiceTest`.
5. Update [`REQUIREMENTS.md`](REQUIREMENTS.md) and the UML diagram if the design
   changed.

### 5.3 New persisted field

1. Update the entity, its constructor and accessors.
2. Append the column to the CSV header and to `findAll` / `saveAll` in the matching
   repository.
3. Keep column order stable; existing files must still load.
4. Add a persistence test in the service test class.
5. Update [`DATABASE_AND_PERSISTENCE.md`](DATABASE_AND_PERSISTENCE.md).

## 6. Git workflow

`main` is the stable branch; `develop` is the integration branch. Create a new
focused branch from `develop` for every feature, bug fix, documentation change or
maintenance task. Open pull requests into `develop`; only maintainers promote
reviewed work from `develop` to `main`. Do not commit directly to either shared
branch.

```bash
git switch develop
git pull origin develop
git switch -c feature/student-search
# implement and verify
mvn clean verify
git add src docs
git commit -m "feat: add student search by major"
git push -u origin feature/student-search
# Open a pull request from feature/student-search to develop.
```

- Branch prefixes: `feature/`, `fix/`, `docs/`, `chore/`.
- Keep each branch and pull request focused on one topic.
- Commit messages: `feat:`, `fix:`, `test:`, `docs:`, `refactor:`, `chore:`.
- Run `mvn clean verify`; GUI changes also need a manual check in a desktop session.
- Never commit `target/`, IDE files or `data/*.csv`.
- Update [`TEAM_CONTRIBUTION.md`](TEAM_CONTRIBUTION.md) with what you actually did.
- See [`CONTRIBUTING.md`](../CONTRIBUTING.md) for the full review workflow.

## 7. Definition of done

A task is done when:

- [ ] it compiles with `mvn clean test`;
- [ ] new rules have tests and the full suite is green;
- [ ] the GUI still runs and the affected screen was checked manually;
- [ ] public code has JavaDoc where behaviour is not obvious;
- [ ] affected documentation (requirements, architecture, UML, persistence, GUI) is
      updated;
- [ ] no debug code or commented-out blocks remain;
- [ ] the contribution log is updated.

## 8. Common mistakes to avoid

| Mistake | Why it hurts | Correct approach |
| --- | --- | --- |
| Capacity check inside a controller | Rule bypassed by tests/other screens | `Course.enroll` / `RegistrationService` |
| Editing `data/*.csv` while the app runs | In-memory cache overwrites the file | Close the app, edit, restart |
| Catch `IOException` and ignore it | Silent data loss | Wrap in `DataAccessException` |
| Hard-coded sample rows in a controller | Data duplicated, impossible to change | Services + `SampleDataInitializer` |
| Adding a database/Spring “because real apps use it” | Out of scope, harder to explain | Documented CSV decision |
| Storing a student list in `courses.csv` | Two sources of truth | `registrations.csv` only |
