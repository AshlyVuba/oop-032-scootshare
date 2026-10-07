#!/usr/bin/env bash
#
# Bumps the project's semantic version in pom.xml.
#
#   usage:  bash scripts/bump-version.sh <major|minor|patch> [pom-file]
#
# TODO (Q5.4): complete this script so that it:
#
#   1. reads the project's own version from the pom (the FIRST <version> tag in
#      the file, in the form MAJOR.MINOR.PATCH, e.g. 1.4.9);
#   2. bumps it according to the first argument, following semantic versioning:
#        patch  1.4.9 -> 1.4.10
#        minor  1.4.9 -> 1.5.0     (patch resets to 0)
#        major  1.4.9 -> 2.0.0     (minor and patch reset to 0)
#   3. rewrites ONLY that one line of the pom. Dependency and plugin versions
#      (which are also <version> tags) must not change;
#   4. prints  "<old> -> <new>"  to standard output, e.g.  1.4.9 -> 1.4.10
#   5. uses pom.xml in the current directory when no second argument is given;
#   6. on bad input, prints a message to STANDARD ERROR and exits:
#        - missing or unknown bump type ........ message starting "Usage:", exit status 2
#        - pom file does not exist ............. an error message, exit status 1
#
# You may not change scripts/check-bump.sh.

echo "TODO: implement me"
exit 0
