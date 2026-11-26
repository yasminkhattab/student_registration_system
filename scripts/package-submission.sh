#!/usr/bin/env bash
#
# Builds the source-code ZIP required by the CS113 brief.
#
# Usage:
#   ./scripts/package-submission.sh <section> <group>
#   ./scripts/package-submission.sh CB14 A
#
# The archive contains the files committed on the current branch (HEAD), without
# target/, local CSV data or IDE metadata, and is placed in dist/ as
# CS113_<Section>_Project_Group<Group>.zip.
#
# Commit your work before running this script; uncommitted changes are not included.

set -euo pipefail

if [ "$#" -ne 2 ]; then
    cat >&2 <<'EOF'
Usage: ./scripts/package-submission.sh <section> <group>

Examples:
  ./scripts/package-submission.sh CB14 A
  ./scripts/package-submission.sh CB14 3

Arguments may contain letters, digits, '-' and '_'.
EOF
    exit 2
fi

section="$1"
group="$2"

if ! [[ "$section" =~ ^[A-Za-z0-9_-]+$ ]] || ! [[ "$group" =~ ^[A-Za-z0-9_-]+$ ]]; then
    echo "Error: section and group may contain only letters, digits, '-' and '_'." >&2
    exit 2
fi

repo_root="$(git rev-parse --show-toplevel 2>/dev/null)" || {
    echo "Error: run this script inside the project Git repository." >&2
    exit 1
}
cd "$repo_root"

if ! git rev-parse --verify HEAD >/dev/null 2>&1; then
    echo "Error: the repository has no commit yet. Commit the project first." >&2
    exit 1
fi

if ! git diff --quiet || ! git diff --cached --quiet; then
    echo "WARNING: uncommitted changes will NOT be included (the ZIP uses HEAD)." >&2
fi

archive_name="CS113_${section}_Project_Group${group}"
output_dir="$repo_root/dist"
output_file="$output_dir/${archive_name}.zip"

mkdir -p "$output_dir"
if [ -e "$output_file" ]; then
    echo "Replacing existing archive: $output_file"
    rm -f "$output_file"
fi

git archive --format=zip --prefix="${archive_name}/" --output="$output_file" HEAD

# Fail fast if a required entry is missing.
required_entries=(
    "${archive_name}/pom.xml"
    "${archive_name}/README.md"
    "${archive_name}/src/main/java/com/studentregistration/Main.java"
    "${archive_name}/src/main/resources/fxml/login.fxml"
    "${archive_name}/src/main/resources/images/university-mark.png"
)
listing="$(unzip -Z1 "$output_file")"
for entry in "${required_entries[@]}"; do
    if ! grep -qxF "$entry" <<<"$listing"; then
        echo "Error: required file missing from archive: $entry" >&2
        exit 1
    fi
done

if grep -q "^${archive_name}/target/" <<<"$listing"; then
    echo "Error: target/ must not be included in the submission archive." >&2
    exit 1
fi

if grep -q "^${archive_name}/data/.*\.csv$" <<<"$listing"; then
    echo "Error: local CSV data must not be included in the submission archive." >&2
    exit 1
fi

file_count="$(wc -l <<<"$listing" | tr -d ' ')"
echo
echo "Created $output_file"
echo "Entries: $file_count"
echo
echo "Next steps:"
echo "  1. Extract into a clean directory and run: mvn clean test && mvn javafx:run"
echo "  2. Open the project once in IntelliJ IDEA, Eclipse or NetBeans (CLO 2.3)."
echo "  3. Submit the ZIP and the report PDF on Blackboard before 12 Dec 2026, 11:59 PM."
