# CurseForge page

What goes into the Kayle project on CurseForge. Upload `Kayle-<version>-all.jar` from a tagged release, not a
local build. Logo: `docs/curseforge/logo.png`.

- Name: Kayle
- Summary: The Kotlin library behind the Degoos Team's Hytale plugins. Install it if a plugin asks for it.
- Category: Library / API
- License: MIT
- Source: https://github.com/Degoos-Team/Kayle
- Issues: https://github.com/Degoos-Team/Kayle/issues

## Description

Kayle is a library, not something you play with. If a plugin says it needs Kayle, drop the JAR into `mods/` next to
it and you're done. No config.

Why it exists: the Degoos Team writes its plugins in Kotlin, and shipping the whole Kotlin runtime inside every plugin
got old fast. Kayle carries it once for the whole server (standard library, coroutines, serialization), so the plugins on top stay
small.

For plugin developers it also has the bits we kept rewriting across Degoos plugins:

- a plugin base class with a coroutine scope that gets cancelled when the plugin shuts down
- Hytale codecs built on kotlinx.serialization (vectors and transforms included)
- message and title helpers
- player avatar / profile lookups

To build against it, use `com.degoos:kayle` and add `"Degoos:Kayle": "*"` to your manifest's dependencies.

Made by Gael Rial and Antonio Terrero (Degoos Team). Bugs and ideas go to GitHub issues.
