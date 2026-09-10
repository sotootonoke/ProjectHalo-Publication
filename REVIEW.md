# Project Halo review — September 10, 2026

## Privacy copy

Copied tracked source files only, excluding the original Git history and unrelated workspaces. Removed the computer-specific documentation link. Reset the submission manifest to prevent submitting the old history accidentally. The clean copy is saved in the private sotootonoke/ProjectHalo-Publication repository with GitHub noreply commit attribution. The original source was not edited.

## Verification

The source compiles with locally cached RuneLite dependencies in offline mode; current-release compatibility is not yet verified.

All six JUnit behavior tests pass with `./gradlew test --offline --no-daemon`: protection-prayer colors and opacity; turning off the aura on individual prayer updates; excluding other players, null players, dead players, and unavailable models; clearing the aura on a varplayer update; activating and switching prayers on varplayer updates; and ignoring varplayer updates while logged out. The clearing regression failed before the production fix.

ProjectHaloPluginTest is only a development launcher. Actual automated checks are in ProjectHaloBehaviorTest. Its proxies and reflection are confined to test sources. No client or account was launched.

## Stale-aura finding

ProjectHaloPlugin.onVarbitChanged previously handled only the three individual prayer varbit IDs. RuneLite documents that a varplayer event has varbitId -1. The handler ignored this event even when the backing prayer state changed. The regression activates a prayer, clears its underlying state, sends a varplayer event, and checks that the aura disappears.

Implemented correction: conservatively refresh prayer state on varplayer events as well as the three prayer varbit IDs. The refresh reads at most three prayer values, preserving the existing rendering path and logged-in guard. It performs no game actions. This fixes the reproduced event-filter regression; it does not establish all in-game lifecycle behavior.

API evidence: https://static.runelite.net/runelite-api/apidocs/net/runelite/api/events/VarbitChanged.html

## Remaining verification

- Test delayed local-player availability at login or world hop: refresh currently gives up when the player is null and has no periodic retry.
- Test actual visual rendering, depletion, rapid prayer changes, loading, death, and login/world-hop behavior in game.
- Verify the current RuneLite dependency build and Gradle wrapper integrity before publication.
- The inspected production code reads local-player prayer state and renders an outline. No network calls, input automation, prayer advice, or targeting logic were found. This is not a guarantee of Plugin Hub acceptance; maintainers decide approval.

Rules reviewed: https://github.com/runelite/plugin-hub#reviewing and https://github.com/runelite/runelite/wiki/Rejected-or-Rolled-Back-Features
