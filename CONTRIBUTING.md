# Contributing

## Bug reports

Please open an issue on the [issue tracker](https://github.com/MemoriesOfTime/Pokkit/issues) and include:

* the Pokkit version (shown in the startup log and in `UnsupportedOperationException` messages),
* the Nukkit-MOT build you are running,
* the name and version of the Bukkit plugin that fails,
* the full server log, including the stack trace,
* what you expected to happen and what happened instead.

Keep in mind that Pokkit is an incomplete implementation of the Spigot API. An `UnsupportedOperationException` for a method that has no Pokkit implementation yet is expected behaviour, not necessarily a bug. See the [compatibility notes](./README.md#compatibility) in the README.

## Pull requests

Contributions are very much welcomed. Implementing a missing method or event is usually a small change, so do not hesitate to send a pull request instead of only reporting the problem.

Before you start:

* [`IMPL_EXAMPLES.md`](./IMPL_EXAMPLES.md) explains how Bukkit methods are implemented.
* [`Events.md`](./Events.md) walks through adding a Nukkit-to-Bukkit event translation.
* [`AGENTS.md`](./AGENTS.md) documents build commands, project layout and code style in more detail.

Build and test your changes with:

```bash
mvn clean install
```

This compiles the sources, runs the JUnit tests and packages the plugin JAR into `target/`. Please make sure `mvn test` passes before opening the pull request, and add a test when you change a converter or another piece of logic that can be tested without a running server.

Don't worry too much about code formatting, just make sure that the change is readable and consistent with its surroundings. For reference: we use tabs instead of spaces for indenting, and we place opening braces (`{`) on the same line.
