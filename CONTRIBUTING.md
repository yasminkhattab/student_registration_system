# Contributing

Thanks for helping improve the University Student Course Registration System.
This project uses a small review workflow so changes can be discussed and checked
before they reach the shared branches.

## Branch workflow

- `main` contains reviewed, stable project versions.
- `develop` is the integration branch for accepted work.
- Create a new branch for every change. Do not commit directly to `main` or
  `develop`.
- Open feature, fix, documentation and maintenance pull requests against
  `develop`. A maintainer promotes a reviewed `develop` branch to `main` when a
  release is ready.

Start from the latest `develop` branch:

```bash
git fetch origin
git switch develop
git pull --ff-only origin develop
git switch -c feature/course-search-filters
```

If `develop` has not been created on GitHub yet, a maintainer should create it from
`main` and publish it once:

```bash
git switch main
git pull --ff-only origin main
git switch -c develop
git push -u origin develop
```

Use one of these branch prefixes:

| Work | Prefix | Example |
| --- | --- | --- |
| New behavior | `feature/` | `feature/course-search-filters` |
| Bug fix | `fix/` | `fix/registration-capacity-check` |
| Documentation | `docs/` | `docs/update-installation-guide` |
| Maintenance | `chore/` | `chore/update-javafx-version` |

Keep a branch focused on one change. If work takes a while, sync with `develop`
before opening the pull request and resolve conflicts on your branch.

## Before opening a pull request

1. Run `mvn clean verify` and fix failures. This compiles the project, runs the
   JUnit suite and packages the application.
2. For GUI changes, run the app with `mvn javafx:run` in a graphical desktop session
   and check the affected screens and navigation.
3. Update relevant documentation, including requirements, GUI design, persistence
   or the changelog when the change affects them.
4. Check `git status` and ensure you have not added `target/`, IDE files, secrets,
   or local `data/*.csv` files.
5. Use a clear commit subject with one of these prefixes:
   `feat:`, `fix:`, `docs:`, `test:`, `refactor:` or `chore:`.

Then push your branch and open a pull request into `develop`:

```bash
git add <files>
git commit -m "feat: add course search filters"
git push -u origin feature/course-search-filters
```

Set the pull request **base branch to `develop`**, fill out the pull request
template, and link related issues with phrases such as
`Closes #12` where applicable. A maintainer reviews and merges approved changes.

## Reporting issues

Use the GitHub **Bug report**, **Feature request** or **Documentation improvement**
issue form. Include steps to reproduce bugs and the expected behavior. Remove
personal data, passwords, tokens and other secrets from logs or screenshots.

## Project conventions

- Java 21, four-space indentation, one public top-level type per file.
- Keep business rules in models or services, not JavaFX controllers.
- Add or update JUnit tests for changed business rules and persistence behavior.
- Preserve the existing CSV format unless a migration or compatibility plan is
  included.
- Do not add production authentication claims to the demo login flow; it is a
  course-project simplification.

See [`docs/DEVELOPMENT_GUIDE.md`](docs/DEVELOPMENT_GUIDE.md) for package ownership,
coding conventions and feature-specific checklists.

## Maintainer branch settings

The files in `.github/` provide issue forms, a pull request template and a Maven CI
workflow. GitHub branch protection is configured in repository settings, not in
these files. For `main` and `develop`, maintainers should require pull requests,
require the **Maven / verify** check, dismiss stale approvals after new commits,
and block force pushes and branch deletion.
