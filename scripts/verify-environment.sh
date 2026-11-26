#!/usr/bin/env bash
# ---------------------------------------------------------------------------
# Prints the local toolchain versions and checks the Java requirement.
# Usage: ./scripts/verify-environment.sh
# ---------------------------------------------------------------------------
set -uo pipefail

cd "$(dirname "$0")/.."

if [[ -z "${JAVA_HOME:-}" && -d "$HOME/.local/opt/jdk-21" ]]; then
    export JAVA_HOME="$HOME/.local/opt/jdk-21"
    export PATH="$JAVA_HOME/bin:$PATH"
fi

echo "--- Java runtime -------------------------------------------------"
java -version 2>&1 || echo "java not found"

echo "--- Java compiler ------------------------------------------------"
javac -version 2>&1 || echo "javac not found"

echo "--- Maven --------------------------------------------------------"
mvn -version 2>&1 || echo "mvn not found"

echo "--- Git ----------------------------------------------------------"
git --version 2>&1 || echo "git not found"

echo "--- Display ------------------------------------------------------"
echo "DISPLAY=${DISPLAY:-<empty>}  WAYLAND_DISPLAY=${WAYLAND_DISPLAY:-<empty>}"

echo "--- Project files ------------------------------------------------"
ls -1 pom.xml README.md .gitignore 2>/dev/null

echo "--- Data files ---------------------------------------------------"
ls -1 data 2>/dev/null || echo "data directory missing"
