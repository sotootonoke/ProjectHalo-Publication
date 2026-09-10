# Publication preparation

This source copy excludes the original Git history. It has not been published or committed to a new repository.

Before publication:

1. Resolve the failing state-update regression described in REVIEW.md.
2. Run tests against current RuneLite dependencies and complete in-game visual testing.
3. Initialize a new repository using the exact GitHub-provided noreply email from the account's email settings for author and committer. Do not merge the old history into this copy.
4. Publish to a new repository only after approval.
5. Fill in plugin-hub-manifest-template.properties with the new repository and its reviewed commit.
6. Submit that manifest to Plugin Hub and follow maintainer review.

Public author handle and license attribution are preserved. Do not commit local build outputs or Gradle caches.

Official instructions: https://github.com/runelite/plugin-hub#submitting-a-plugin
