# Kayle

Kayle is a Kotlin-based framework for developing plugins and mods for the Hytale server. It provides a set of utilities and abstractions to simplify server-side development.

## Prerequisites

- **Java 23**: Required for building and running the project. Ensure JDK 23 is installed and configured.
- **Gradle**: The project uses Gradle for build management. The included Gradle Wrapper (`gradlew` or `gradlew.bat`) can be used.
- **Hytale Server**: A local installation of the Hytale server is needed for compilation and deployment. The server JAR is used as a compile-time dependency.

## Setup

1. Clone the repository:
   ```
   git clone <repository-url>
   cd kayle
   ```

2. Configure Gradle properties:
    - Create or edit `gradle.properties` in the project root.
    - Set the path to your Hytale installation:
   
      _The `hytale.path` should point to the root of your Hytale installation where `Server/HytaleServer.jar` is located._
      ```
      hytale.path=C:/path/to/hytale/installation
      ```
      Example paths:
        - Windows: `hytale.path=C:\Users\<username>\AppData\Roaming\Hytale\install\release\package\game\latest`
        - Linux: `hytale.path=/home/<username>/.local/share/Hytale/install/release/package/game/latest`
    - Optionally, set the server output directory (defaults to `build/default-mods` if not set):
      ```
      hytale.server=C:/path/to/server/directory
      ```
      Example paths:
        - Windows: `hytale.server=C:\Users\<username>\AppData\Roaming\Hytale\UserData`
        - Linux: `hytale.server=/home/<username>/.local/share/Hytale/UserData`

## Project Structure

- **Core**: The main library module containing the framework's core functionality.
- **Examples**: Sample plugins demonstrating how to use the Kayle framework.

## Building

To build all modules:

```
./gradlew build
```

This will compile the Kotlin code and run tests.

## Deployment

To build and deploy plugins to the configured server directory:

```
./gradlew deployPlugin
```

This task is available for modules that apply the Shadow plugin (e.g., Core and Examples). It creates a fat JAR and copies it to the `mods` directory of the specified server path.

## Usage

### Creating a Plugin

1. Create a new module or add to an existing one.
2. Apply the necessary plugins in `build.gradle.kts`:
   ```kotlin
   plugins {
       alias(libs.plugins.kotlin.jvm)
       alias(libs.plugins.kotlin.serialization)
       alias(libs.plugins.shadow)
   }
   ```
3. Add dependencies:
   ```kotlin
   dependencies {
       compileOnly(project(":Core"))
       compileOnly(files(hytaleServerExecutablePath))
   }
   ```
4. Implement your plugin logic using the Kayle framework APIs.

Refer to the Examples module for sample implementations.

## Contributing

Contributions are welcome! Please ensure code follows Kotlin coding standards and includes appropriate tests.

## License

[Specify license if applicable]
