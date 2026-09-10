# Project Halo

Project Halo is a visual-only RuneLite external plugin that outlines the local player when a protection prayer is active:

- Protect from Melee: red aura
- Protect from Missiles: green aura
- Protect from Magic: blue aura
- No protection prayer: no aura

The implementation is intentionally conservative:

- No automation
- No clicking or menu interaction
- No prayer switching
- No packet modification
- No private-data reads
- No hidden-information prediction
- No alerts telling the player what to pray
- No network calls or telemetry
- Local player only

## Project Structure

```text
project-halo/
├── .gitignore
├── README.md
├── build.gradle
├── runelite-plugin.properties
├── settings.gradle
├── src
│   ├── main
│   │   └── java
│   │       └── com
│   │           └── protectionaura
│   │               ├── ProjectHaloConfig.java
│   │               ├── ProjectHaloOverlay.java
│   │               └── ProjectHaloPlugin.java
│   └── test
│       └── java
│           └── com
│               └── protectionaura
│                   └── ProjectHaloPluginTest.java
```

## Class-by-Class Explanation

### `ProjectHaloPlugin`

Main plugin entry point.

- Registers and removes the overlay during startup and shutdown
- Tracks the local player's active protection prayer using RuneLite `Prayer#getVarbit()` and `Client#getVarbitValue(...)`
- Rebuilds cached aura colors when config changes
- Maintains no gameplay automation or input logic

### `ProjectHaloConfig`

Plugin configuration contract.

- Soft enable/disable toggle
- Aura thickness
- Aura opacity
- Custom melee/range/magic colors

### `ProjectHaloOverlay`

Scene overlay renderer.

- Uses RuneLite's `ModelOutlineRenderer`
- Renders only while logged in
- Draws the local player only

### `ProjectHaloPluginTest`

Development launcher entry point.

- Loads the external plugin into RuneLite dev mode
- Used by the Gradle `run` task

## Build Instructions

1. Install a JDK that supports `--release 11` such as JDK 11 or JDK 17.
2. From the project root, run:

```bash
./gradlew test
```

To launch RuneLite in developer mode with the plugin loaded:

```bash
./gradlew run
```

To build a fat jar for local testing:

```bash
./gradlew shadowJar
```

## RuneLite Development Setup

1. Clone or open this project in IntelliJ IDEA.
2. Let the Gradle wrapper import dependencies from `https://repo.runelite.net` and Maven Central.
3. Confirm the project SDK is set to a JDK compatible with Java 11 bytecode output.
4. Run the `run` Gradle task or `ProjectHaloPluginTest.main()`.
5. Open RuneLite's configuration panel and enable `Project Halo`.
6. Toggle a protection prayer and confirm the local-player aura changes color immediately.

If you use a Jagex account in dev mode, follow RuneLite's official development-client guidance for account login setup.

## Usage

1. Enable the plugin in RuneLite.
2. Activate a protection prayer:
   - Protect from Melee -> red aura
   - Protect from Missiles -> green aura
   - Protect from Magic -> blue aura
3. Disable the prayer to remove the aura immediately.

## Compliance Checklist

- Uses RuneLite-supported plugin, config, event bus, and overlay APIs only
- Uses `Prayer#getVarbit()` plus `Client#getVarbitValue(...)` for local prayer state
- Renders the local player only
- No menu modification
- No mouse or keyboard injection
- No automatic prayer logic
- No target selection
- No interaction with NPC or player clicks
- No hidden-information inference
- No background services, telemetry, or external network traffic
- No unsafe dependencies or obfuscation
- Clean startup and shutdown with overlay add/remove symmetry

## Manual Test Plan

1. Start RuneLite with `./gradlew run`.
2. Log in and enable Project Halo.
3. Stand idle with no protection prayer active.
   Expected: no aura.
4. Activate Protect from Melee.
   Expected: red aura on the local player only.
5. Switch to Protect from Missiles.
   Expected: red disappears immediately, green appears immediately.
6. Switch to Protect from Magic.
   Expected: green disappears immediately, blue appears immediately.
7. Turn the prayer off.
   Expected: aura disappears immediately.
8. Change thickness and opacity in config.
   Expected: overlay updates without restart.
9. Change melee/range/magic colors in config.
   Expected: overlay updates without restart.
10. Disable the config `Enable aura`.
   Expected: no aura, no restart required.

## Edge-Case Test Plan

1. Login screen:
   Expected: no exceptions, no overlay.
2. World hop:
   Expected: aura clears during transition and resumes cleanly after login.
3. Teleport:
   Expected: no flicker beyond normal scene reload behavior.
4. Region load or map load:
   Expected: no exceptions, no stale aura.
5. Death:
   Expected: no crash, aura disappears while the player model is unavailable or dead.
6. Respawn:
   Expected: aura resumes only if a supported protection prayer is active.
7. Plugin enable/disable:
   Expected: overlay adds and removes cleanly.
8. Stand near other players in a populated area:
   Expected: no other players ever receive an aura.

## Known Limitations

- This plugin intentionally renders the local player only.
- This plugin does not attempt to distinguish PvP and non-PvP contexts.
- It intentionally provides no hinting, alerts, or recommendations about which prayer to use.
- The visual style is outline-based; it does not modify the underlying player model or game state.

## Debugging Guide

If the aura does not appear:

1. Confirm the plugin is enabled in RuneLite.
2. Confirm `Enable aura` is on inside the plugin config.
3. Confirm you are logged in, not at the login screen or mid-hop.
4. Confirm one of the three supported protection prayers is active.
5. Increase opacity and thickness to rule out an overly subtle config.
6. Check that another plugin is not visually covering the outline.
7. Re-run with `./gradlew run --stacktrace` or `./gradlew test --stacktrace` if the project fails to start or build.

## Final Acceptance Checklist

- Plugin compiles with Gradle
- Plugin loads in RuneLite dev mode
- No aura when no protection prayer is active
- Red aura only for Protect from Melee
- Green aura only for Protect from Missiles
- Blue aura only for Protect from Magic
- Aura disappears immediately when prayer is disabled
- No aura is ever rendered on other players
- No clicks, menu edits, automation, or gameplay actions occur
- Stable across login, logout, death, teleport, hop, and loading boundaries
- README documents installation, use, configuration, testing, and compliance
