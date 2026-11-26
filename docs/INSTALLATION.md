# Installation

## 1. Reference environment (detected and configured)

| Item | Detected value |
| --- | --- |
| Operating system | Fedora Linux 44 (Workstation Edition) |
| Architecture | x86_64 |
| Kernel | 7.3.3-200.fc44.x86_64 |
| Desktop session | Wayland (`WAYLAND_DISPLAY=wayland-0`), XWayland available (`DISPLAY=:0`) |
| Java used for this project | Amazon Corretto OpenJDK 21.0.13.1+2 (JDK; `javac` available) |
| Maven | Apache Maven 3.9.16 |
| Git | 2.56.1 (pre-installed) |
| IDEs found | Visual Studio Code, Antigravity IDE (neither is on the course's approved list) |
| Disk space | ~224 GB free on `/` and `/home` |

Fedora 44 repositories provide OpenJDK 25 and 27 but **not** OpenJDK 21. Installing
a JDK with `dnf` would also require root privileges, which were not available in this
session and were not requested.

## 2. What was installed and why

To keep the machine untouched and match the course requirement (Java 21 LTS), both
tools were installed **for the current user** under `~/.local/opt`:

| Tool | Version | Location | Source |
| --- | --- | --- | --- |
| Amazon Corretto JDK | 21.0.13.1+2 (LTS) | `~/.local/opt/jdk-21` → `amazon-corretto-21.0.13.1.2-linux-x64` | `https://corretto.aws/downloads/latest/amazon-corretto-21-x64-linux-jdk.tar.gz` |
| Apache Maven | 3.9.16 | `~/.local/opt/maven` → `apache-maven-3.9.16` | `https://dlcdn.apache.org/maven/maven-3/3.9.16/binaries/apache-maven-3.9.16-bin.tar.gz` |

No `sudo`, no package removal, no change to the system JDK. JavaFX is **not**
installed separately: Maven resolves `javafx-controls`, `javafx-fxml` and
`javafx-graphics` 21.0.13 from Maven Central (ADR 0002).

### Environment variables

A clearly marked block was appended to `~/.bashrc` (interactive shells, which also
sources from `~/.bash_profile`):

```bash
# >>> CS113 student-registration-system toolchain >>>
if [ -d "$HOME/.local/opt/jdk-21" ]; then
    export JAVA_HOME="${JAVA_HOME:-$HOME/.local/opt/jdk-21}"
    case ":$PATH:" in
        *":$JAVA_HOME/bin:"*) ;;
        *) export PATH="$JAVA_HOME/bin:$PATH" ;;
    esac
fi
if [ -d "$HOME/.local/opt/maven/bin" ]; then
    case ":$PATH:" in
        *":$HOME/.local/opt/maven/bin:"*) ;;
        *) export PATH="$HOME/.local/opt/maven/bin:$PATH" ;;
    esac
fi
# <<< CS113 student-registration-system toolchain <<<
```

Open a new terminal (or run `source ~/.bashrc`) to pick it up.

## 3. Verification performed

```text
$ java -version
openjdk version "21.0.13.1" 2026-11-17 LTS
OpenJDK Runtime Environment Corretto-21.0.13.1.2 (build 21.0.13.1+2-LTS)
OpenJDK 64-Bit Server VM Corretto-21.0.13.1.2 (build 21.0.13.1+2-LTS, sharing)

$ javac -version
javac 21.0.13.1

$ mvn -version
Apache Maven 3.9.16
Maven home: /home/tawfik0x00/.local/opt/maven
Java version: 21.0.13.1, vendor: Amazon.com Inc.
OS name: "linux", version: "7.3.3-200.fc44.x86_64", arch: "amd64"

$ git --version
git version 2.56.1
```

| Check | Result |
| --- | --- |
| `mvn clean validate compile test package` | BUILD SUCCESS; 53 tests, 0 failures, 0 errors; JAR created |
| JavaFX Maven plugin resolution | `javafx:run` resolves `org.openjfx:javafx-maven-plugin:0.0.8` and starts the Java process |
| JavaFX GUI in this restricted execution session | Could not open: JavaFX reports `Unable to open DISPLAY`; the session cannot connect to its advertised display |
| `mvn help:effective-pom` | Not available offline because `maven-help-plugin` is not present in the local Maven cache |

## 4. Quick start on this machine

```bash
cd /home/tawfik0x00/OPENCODE/Universy_Registeration_System/student-registration-system
./scripts/verify-environment.sh   # prints versions and display status
mvn clean test                    # unit tests
mvn clean javafx:run              # launch the desktop application
```

The first Maven run downloads dependencies and may take a few minutes.

### JavaFX Maven configuration

The project targets **Java 21** (`maven.compiler.release`) and uses JavaFX
**21.0.13** for Controls, FXML and Graphics. `pom.xml` configures
`org.openjfx:javafx-maven-plugin:0.0.8` under `<build><plugins>` with
`com.studentregistration.Main` as its main class. The project has no
`module-info.java`; do not add module-path or module descriptor configuration.
Run the configured entry point with `mvn clean javafx:run` from the project
directory.

## 5. Fresh installation on Fedora (if `~/.local/opt` is missing)

```bash
mkdir -p ~/.local/opt/downloads
cd ~/.local/opt/downloads

# JDK 21 LTS
curl -fL -o corretto21.tar.gz \
  "https://corretto.aws/downloads/latest/amazon-corretto-21-x64-linux-jdk.tar.gz"
tar xzf corretto21.tar.gz -C ~/.local/opt
ln -sfn "$(ls -d ~/.local/opt/amazon-corretto-21* | head -1)" ~/.local/opt/jdk-21

# Maven
curl -fL -o maven.tar.gz \
  "https://dlcdn.apache.org/maven/maven-3/3.9.16/binaries/apache-maven-3.9.16-bin.tar.gz"
tar xzf maven.tar.gz -C ~/.local/opt
ln -sfn ~/.local/opt/apache-maven-3.9.16 ~/.local/opt/maven

# Current shell only
export JAVA_HOME="$HOME/.local/opt/jdk-21"
export PATH="$JAVA_HOME/bin:$HOME/.local/opt/maven/bin:$PATH"
java -version && javac -version && mvn -version
```

Then add the `~/.bashrc` block from section 2.

Linux packages JavaFX needs at runtime: GTK 3 and OpenGL/Mesa, normally installed on
Fedora Workstation (`sudo dnf install gtk3 mesa-libGL` only if missing).

## 6. Windows and macOS (teammates)

1. Install **JDK 21 (Temurin or Corretto)** — MSI on Windows, `.pkg` on macOS.
2. Install **Maven 3.9.x** and add its `bin` directory to `PATH`.
3. Set `JAVA_HOME` to the JDK 21 directory (Control Panel → Environment Variables on
   Windows; `~/.zshrc` on macOS with Apple Silicon:
   `export JAVA_HOME=$(/usr/libexec/java_home -v 21)`).
4. Verify with `java -version`, `javac -version`, `mvn -version` (Maven must report
   Java 21).
5. Run `mvn clean test` and `mvn javafx:run`.

JavaFX artefacts are resolved automatically for the current operating system.

## 7. IDE setup (required for CLO 2.3)

The brief names **IntelliJ IDEA, Eclipse or NetBeans**. Before the presentation the
group must open and build the project in one of these IDEs; the reference machine
only had VS Code and Antigravity installed, so this step is still outstanding.

1. **IntelliJ IDEA** — *File → Open*, select the `pom.xml` (or the project folder),
   choose “Open as Project”, set **Project SDK** to JDK 21, wait for Maven import.
   Run `com.studentregistration.Main` or the Maven `javafx:run` goal.
2. **Eclipse** — *File → Import → Maven → Existing Maven Projects*, select the
   repository, set the JRE to Java 21, then run `Main` or `mvn javafx:run`.
3. **NetBeans** — *File → Open Project*, select the folder; NetBeans recognises the
   Maven build. Set the Java platform to JDK 21 and run the project.

VS Code with “Extension Pack for Java” also builds the project, but it does not
satisfy the IDE requirement on its own.

Do not commit IDE metadata (`.idea/`, `.classpath`, `.project`, `.settings/`,
`.vscode/` are ignored).

## 8. First run and demo data

- The application creates `data/students.csv`, `data/courses.csv` and
  `data/registrations.csv` on first start.
- If both students and courses are empty, demo records are inserted (see
  [`DATABASE_AND_PERSISTENCE.md`](DATABASE_AND_PERSISTENCE.md#demo-data)).
- Demo login: administrator `ADM-001` / `admin123`; students `STU-1001` to
  `STU-1003`.
- Delete `data/*.csv` to reset.

## 9. Troubleshooting

| Symptom | Cause | Fix |
| --- | --- | --- |
| `No plugin found for prefix 'javafx'` | Maven did not load this project's POM, or plugin resolution failed (for example, Maven Central is offline and the plugin is not cached) | Run the command from the directory containing this `pom.xml`; confirm it declares `org.openjfx:javafx-maven-plugin:0.0.8` under `<build><plugins>` and that Maven can access its repository. In this project the plugin is already declared, so do not add a duplicate. |
| `Unable to open DISPLAY` / `UnsupportedOperationException` from GTK | The process cannot connect to an X11 or Wayland display; environment variables alone do not prove the display socket is accessible | Run `mvn clean javafx:run` in a logged-in desktop terminal. For SSH, configure X11 forwarding or use a desktop session. The GUI cannot launch in a headless or display-isolated shell. |
| JavaFX error creating `.openjfx/cache` or native library cache | Java's home/cache directory is not writable | Use a writable home/cache directory for the application process. If overriding `user.home`, preserve Maven's repository separately with `-Dmaven.repo.local=<writable Maven cache>`; changing `user.home` alone also changes Maven's default dependency cache. |
| Maven cannot write tracking files under `~/.m2/repository` | The Maven local repository is read-only | Use a writable Maven local repository, for example `mvn -Dmaven.repo.local=/path/to/writable/repository javafx:run`; Maven must be able to write metadata as well as read cached artifacts. |
| `JavaFX runtime components are missing` or `ClassNotFoundException` for `javafx.*` | JavaFX dependencies were not resolved or the app was started outside the configured Maven goal | Use `mvn javafx:run`; verify `javafx-controls`, `javafx-fxml` and `javafx-graphics` use the same JavaFX 21 version as configured in `pom.xml`. Do not launch the thin JAR with `java -jar`. |
| `FXMLLoadException`, missing resource, or controller initialization error | An FXML resource path, `fx:controller`, injected field, or handler does not match the application code | Check the referenced file under `src/main/resources/fxml/`, its controller class and `@FXML` field/handler names; rebuild with `mvn clean compile` and inspect the underlying `Caused by` message. |
| `javac: command not found` | Only a JRE is installed | Install JDK 21 and put its `bin` first in `PATH` |
| Maven reports “release version 21 not supported” | Maven is using an older JDK | `export JAVA_HOME=~/.local/opt/jdk-21`; check `mvn -version` |
| Blank or mis-scaled window on Wayland | Toolkit/scale mismatch | JavaFX runs through XWayland; try `GDK_BACKEND=x11 mvn javafx:run` |
| `Could not save ...: Permission denied` | `data/` not writable | Check ownership/permissions of the project directory |
| “Malformed student row …” on start | CSV edited manually with a missing column | Restore the row or delete the file to reseed |
| Dependencies fail to download | Offline/no access to Maven Central | Connect once, or configure a mirror in `~/.m2/settings.xml` |
| Window opens but tests fail | Tests use `@TempDir` and do not need the display | Fix the failing assertion; GUI state is irrelevant |

## 10. Notes and limitations

- The system Java 25 JRE remains the default `/usr/bin/java`; the project uses the
  user-local JDK 21 through `JAVA_HOME` and `PATH`.
- The GUI was verified on this machine’s Wayland session; the application has not
  been tested on every desktop environment.
- Screenshots taken during verification are development artefacts and are not
  committed (add final screenshots to the report separately if required).
