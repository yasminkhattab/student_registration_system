#!/usr/bin/env bash
# ---------------------------------------------------------------------------
# Launches the JavaFX application with Maven.
# Usage: ./scripts/run.sh
# ---------------------------------------------------------------------------
set -euo pipefail

cd "$(dirname "$0")/.."

# Use the project-local JDK 21 toolchain when it is not already configured.
if [[ -z "${JAVA_HOME:-}" && -d "$HOME/.local/opt/jdk-21" ]]; then
    export JAVA_HOME="$HOME/.local/opt/jdk-21"
    export PATH="$JAVA_HOME/bin:$PATH"
fi

if ! command -v mvn >/dev/null 2>&1; then
    echo "Maven was not found on PATH. See docs/INSTALLATION.md." >&2
    exit 1
fi

if [[ -z "${DISPLAY:-}" && -z "${WAYLAND_DISPLAY:-}" ]]; then
    echo "No graphical session detected (DISPLAY/WAYLAND_DISPLAY are empty)." >&2
    echo "JavaFX needs a desktop session; start the app from the desktop terminal." >&2
    exit 1
fi

exec mvn -q javafx:run
