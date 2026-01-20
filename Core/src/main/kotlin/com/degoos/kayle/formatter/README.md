# MessageFormatter
**Internal text formatting parser for Hytale messages.**

MessageFormatter is the core parsing engine that converts tagged strings into styled Hytale `Message` objects. It handles colors, gradients, text styles, links, and nested formatting through a modular architecture.

---

## Architecture Overview

The formatter uses a stack-based parser that maintains style state while processing tags. When opening tags are encountered, they push a new state onto the stack. Closing tags pop the stack, reverting to the previous style. This allows for unlimited nesting of styles.

The implementation is split across six files, each with a single responsibility:

### MessageFormatter.kt
The public API and main entry point. Contains the parsing loop that iterates through regex matches, delegates tag processing, and builds the final message tree.

**Public API:**
```kotlin
MessageFormatter.parse(text: String): Message
```

### StyleState.kt
Immutable data class representing the current formatting state. Each state contains color information (solid or gradient), text styles (bold, italic, underlined, monospace), and optional link data.

The class uses builder-style methods (`withColor()`, `withBold()`, etc.) that return new instances rather than modifying the existing state. This immutability makes the stack-based parsing safe and predictable.

### TagProcessor.kt
Processes opening tags and returns the updated style state. Maps tag names to their corresponding state transformations. Handles both named color shortcuts (like `<red>`) and explicit tags (like `<color:red>`).

The processor normalizes tag names to lowercase and supports multiple aliases for common operations (e.g., `<b>`, `<bold>` both work).

### NamedColors.kt
Singleton object containing predefined color mappings. Provides 16 named colors matching Minecraft's color scheme: `black`, `dark_blue`, `dark_green`, `dark_aqua`, `dark_red`, `dark_purple`, `gold`, `gray`, `dark_gray`, `blue`, `green`, `aqua`, `red`, `light_purple`, `yellow`, `white`.

### ColorParser.kt
Handles all color-related parsing operations. Converts hex strings (with or without `#`) into `Color` objects, parses colon-separated gradient definitions, and performs color interpolation for gradients.

The interpolation function smoothly transitions between multiple colors based on character position, supporting gradients with any number of color stops.

### MessageStyler.kt
Applies the accumulated style state to text content. For solid colors, it creates a single `Message` with all styles applied. For gradients, it generates individual `Message` objects for each character with interpolated colors, then wraps them in a container.

---

## Parsing Flow

1. Input string is scanned with regex pattern `<(/?)([a-zA-Z0-9_]+)(?::([^>]+))?>`
2. Text before each tag is converted to a styled message using the current state
3. Opening tags are processed by `TagProcessor`, which returns a new state
4. The new state is pushed onto the stack
5. Closing tags pop the stack, reverting to the previous state
6. After all tags are processed, remaining text is converted using the final state
7. All message segments are inserted into a root message container

---

## Supported Tags

### Color Tags
| Tag | Example | Description |
|-----|---------|-------------|
| `<color:X>` | `<color:red>` or `<color:#FF5733>` | Sets text color (named or hex) |
| `<c:X>` | `<c:aqua>` | Short alias for color |
| `<colour:X>` | `<colour:gold>` | British spelling variant |
| Named colors | `<red>`, `<blue>`, etc. | Direct color application without explicit tag |

### Gradient Tags
| Tag | Example | Description |
|-----|---------|-------------|
| `<gradient:X:Y>` | `<gradient:red:blue>` | Two-color gradient |
| `<gradient:X:Y:Z>` | `<gradient:gold:red:black>` | Multi-color gradient with stops |
| `<grnt:X:Y>` | `<grnt:#FF0000:#0000FF>` | Short alias for gradient |

### Style Tags
| Tag | Aliases | Example | Description |
|-----|---------|---------|-------------|
| `<bold>` | `<b>` | `<b>Bold text</b>` | Bold formatting |
| `<italic>` | `<i>`, `<em>` | `<i>Italic text</i>` | Italic formatting |
| `<underline>` | `<u>` | `<u>Underlined</u>` | Underline formatting |
| `<monospace>` | `<mono>` | `<mono>Code</mono>` | Monospace font |

### Special Tags
| Tag | Example | Description |
|-----|---------|-------------|
| `<link:URL>` | `<link:https://example.com>Click</link>` | Clickable hyperlink |
| `<url:URL>` | `<url:https://example.com>Click</url>` | Alias for link |
| `<reset>` | `<b>Bold <reset>normal` | Clears all formatting |
| `<r>` | `<i>Italic <r>normal` | Short alias for reset |
| `</tag>` | `<b>Bold</b> normal` | Closes the most recent matching tag |

---

## Implementation Details

### Tag Pattern
The regex pattern captures three groups:
1. Closing slash (if present)
2. Tag name
3. Tag argument (optional, after colon)

Example matches:
- `<red>` → groups: `null`, `red`, `null`
- `<color:blue>` → groups: `null`, `color`, `blue`
- `</bold>` → groups: `/`, `bold`, `null`
- `<gradient:red:blue>` → groups: `null`, `gradient`, `red:blue`

### State Management
The parser uses a `Deque<StyleState>` as a stack. The bottom of the stack is always the default empty state. Each opening tag pushes a new state derived from the current top. Closing tags pop the stack, but never remove the base state.

Reset tags work differently - they clear the entire stack and push a fresh default state.

### Gradient Rendering
Gradients are applied character-by-character. For each character, the parser calculates a progress value from 0 to 1 based on its position in the string. This progress value is used to interpolate between the gradient's color stops.

For a gradient with colors `[C1, C2, C3]` and 10 characters:
- Character 0: 100% C1
- Character 5: 100% C2
- Character 9: 100% C3
- Other characters: interpolated between adjacent stops

---

## Design Decisions

### Why immutability?
Using immutable `StyleState` objects prevents bugs related to shared mutable state. When a tag modifies the state, a new copy is created rather than mutating the original. This ensures that popping the stack truly reverts to the previous state.

### Why separate files?
Each file handles one specific concern. This makes the code easier to navigate, test, and modify. New features can often be added by changing just one file rather than navigating a monolithic class.

### Why object singletons?
Utility classes like `ColorParser` and `NamedColors` have no instance state and are used throughout the parsing process. Using Kotlin's `object` declaration is more idiomatic than Java's static methods and enables features like interface implementation if needed later.

### Why internal visibility?
The parsing implementation is an internal detail. External code should only interact with `MessageFormatter.parse()`. Marking implementation classes as `internal` prevents accidental dependencies on internal structure and allows refactoring without breaking external code.

---

## Example Parsing

Input: `<red>Hello <bold>World</bold>!</red>`

1. Initial state: `StyleState()` (empty)
2. Match `<red>`: push `StyleState(color=RED)`
3. Process "Hello ": create Message with red color
4. Match `<bold>`: push `StyleState(color=RED, bold=true)`
5. Process "World": create Message with red + bold
6. Match `</bold>`: pop to `StyleState(color=RED)`
7. Process "!": create Message with red color
8. Match `</red>`: pop to `StyleState()` (empty)
9. Return root Message containing all segments

---

## Testing Considerations

Each component can be tested independently:

- **ColorParser**: Test hex parsing, named colors, gradient parsing, interpolation
- **TagProcessor**: Test tag name normalization, state transformations
- **MessageStyler**: Test style application, gradient rendering
- **NamedColors**: Verify color values match Minecraft standards
- **MessageFormatter**: Integration tests for complete parsing scenarios

The modular design makes it easy to mock dependencies and test edge cases in isolation.
