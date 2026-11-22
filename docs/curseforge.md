# CurseForge page

Text and settings for the Kayle project on CurseForge. Kayle is a library: players don't use it directly, server owners
install it because a plugin requires it. Upload the JAR of a tagged release (`Kayle-<version>-all.jar`), never a local
build.

- **Name:** Kayle
- **Summary (short description):** The Kotlin framework behind Degoos plugins: install it when a plugin lists Kayle as a
  dependency.
- **Category:** Library / API
- **License:** MIT
- **Source:** https://github.com/Degoos-Team/Kayle
- **Issues:** https://github.com/Degoos-Team/Kayle/issues
- **Dependencies:** none
- **Game version:** the Hytale server versions the release was tested on

## Description

Kayle is a Kotlin framework for Hytale server plugins. It ships the Kotlin standard library, reflection, coroutines and
kotlinx.serialization once for the whole server, so plugins built on it stay small and share one runtime.

**Server owners:** you only need Kayle when another plugin requires it. Put the JAR in `mods/` next to that plugin; it
has no configuration.

**Plugin authors** get:

- `KotlinPlugin`, a plugin base class with a coroutine scope that is cancelled at shutdown
- Hytale codecs backed by kotlinx.serialization, with serializers for common types (vectors, transforms)
- message and formatting extensions
- avatar and profile helpers

Kayle is published as `com.degoos:kayle` for compiling against; declare `"Degoos:Kayle": "*"` in your plugin's
manifest dependencies.
