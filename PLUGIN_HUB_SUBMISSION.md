# Publication preparation

This source copy excludes the original Git history and is saved in the private GitHub repository sotootonoke/ProjectHalo-Publication. It is not public and has not been submitted to Plugin Hub. Commits use GitHub noreply attribution.

Before publication:

1. The state-update regression described in REVIEW.md is fixed; all six offline behavior tests pass. Preserve these checks when making further changes.
2. Run tests against current RuneLite dependencies and complete in-game visual testing.
3. Keep using the exact GitHub-provided noreply email for author and committer. Do not merge the old history into this copy.
4. Make the clean repository public only after approval and final verification.
5. Fill in plugin-hub-manifest-template.properties with the new repository and its reviewed commit.
6. Submit that manifest to Plugin Hub and follow maintainer review.

Public author handle and license attribution are preserved. Do not commit local build outputs or Gradle caches.

Official instructions: https://github.com/runelite/plugin-hub#submitting-a-plugin
