# Pokkit

[![Build Status](https://img.shields.io/jenkins/build?jobUrl=https%3A%2F%2Fmotci.cn%2Fjob%2FPokkit&style=flat-square)](https://motci.cn/job/Pokkit/)
[![Spigot API](https://img.shields.io/badge/Spigot%20API-1.21.11--R0.2-informational?style=flat-square)](#requirements)
[![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square)](#requirements)
[![License](https://img.shields.io/badge/license-MIT-blue?style=flat-square)](./LICENSE.md)

**Pokkit** is a compatibility layer that lets Bukkit/Spigot plugins run on [Nukkit-MOT](https://github.com/MemoriesOfTime/Nukkit-MOT), a Minecraft Bedrock Edition server. It implements the Spigot Plugin API on top of Nukkit and translates Nukkit events, so a plugin written for Spigot can be loaded on a Bedrock server without changing a single line of code.

> [!WARNING]
> **Pokkit is not suitable for production use.** It is **not** a complete implementation of the Spigot API: only a subset of plugins works, and unsupported methods throw `UnsupportedOperationException`. **Do not expect that Pokkit allows you to run a random Spigot plugin on Nukkit.** Use it for development, testing or hobby servers only — do not run it in production if you depend on Spigot plugins working correctly.

## Table of contents

- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [Compatibility](#compatibility)
- [Contributing](#contributing)
- [Acknowledgements](#acknowledgements)
- [License](#license)

## Features

- **Bukkit/Spigot plugin loader** — loads and enables Bukkit plugins from `plugins/Pokkit/bukkitPlugins/`, honouring `plugin.yml` dependencies and its `load` order (`STARTUP` / `POSTWORLD`).
- **Event translation** — player, entity, inventory, block, world, vehicle, chat, item and lifecycle events are translated from Nukkit to Bukkit and fired on the Bukkit event bus.
- **Commands and permissions** — `plugin.yml` commands are exposed as Bukkit `PluginCommand`s, so custom executors and command aliases keep working, and a Bukkit-compatible `permissions.yml` is honoured.
- **Scoreboard persistence** — main scoreboard data is stored in `scoreboard.yml` and restored on the next start.
- **Type mappings** — items, blocks and enchantments are converted between the Bukkit and Nukkit representations, and Bukkit metadata for players, worlds and blocks is supported.
- **Bundled JDBC drivers** — SQLite and MySQL drivers are shaded in and registered, so plugins can call `DriverManager.getConnection(...)` like they would on Spigot.
- **Floodgate integration** — every player counts as a Bedrock player: Floodgate API lookups work with stable, name-derived UUIDs, and a virtual `floodgate` plugin lets plugins detect Floodgate and declare it as a dependency.

## Requirements

| Component | Version |
|-----------|---------|
| Java | 17 or newer |
| Server | [Nukkit-MOT](https://github.com/MemoriesOfTime/Nukkit-MOT) (`MOT-SNAPSHOT`) |
| Bukkit API | Spigot `1.21.11-R0.2-SNAPSHOT` |

## Installation

1. Download the latest build from the CI job: **<https://motci.cn/job/Pokkit/>**
   (or grab the artifact directly from the [last successful build](https://motci.cn/job/Pokkit/lastSuccessfulBuild/artifact/target/)).
2. Place the `Pokkit-*.jar` file into the `plugins/` directory of your Nukkit-MOT server.
3. Start the server once, then stop it. Pokkit creates a `plugins/Pokkit/` folder.
4. Put every Bukkit/Spigot plugin **into `plugins/Pokkit/bukkitPlugins/`** — not into `plugins/`.
5. Restart the server.

After the first start, the data folder looks like this:

```
plugins/
└── Pokkit/
    ├── config.yml          # Pokkit settings
    ├── permissions.yml     # Bukkit permissions configuration
    ├── scoreboard.yml      # persisted scoreboard data
    └── bukkitPlugins/      # <-- Bukkit/Spigot plugin JARs go here
```

> [!WARNING]
> Bukkit plugins placed directly in `plugins/` are **not** loaded. They must live in `plugins/Pokkit/bukkitPlugins/`.

## Compatibility

Pokkit covers a large part of the Spigot API, but coverage is uneven, so a given plugin may or may not work — which is why it is not suitable for production servers. There is no maintained list of compatible plugins — test your plugin and check the log. Typical symptoms of a missing feature are an `UnsupportedOperationException` naming the Pokkit version, or a method that silently does nothing (particles, for example).

- When a plugin fails, check the server log first: Pokkit reports unimplemented methods there.
- Missing API is usually straightforward to add — see [Contributing](#contributing).

## Contributing

Contributions are welcome, both bug reports and pull requests. Please read [CONTRIBUTING.md](./CONTRIBUTING.md) first.

**Porting or debugging a plugin?** When you hit an unimplemented method or event, implementing it is usually a small change: find the `Pokkit*` adapter class and delegate to the matching Nukkit API.

- [`IMPL_EXAMPLES.md`](./IMPL_EXAMPLES.md) — worked examples of how Bukkit methods are implemented.
- [`Events.md`](./Events.md) — step-by-step guide for adding a new event listener.
- [`AGENTS.md`](./AGENTS.md) — build, test and code-style conventions.

Code style, in short: tabs for indentation, opening braces on the same line.

## Acknowledgements

Pokkit was originally created by [Rutger Kok](https://github.com/rutgerkok); Thanks to everyone who contributed code over the years:

| Year | Contributors |
|------|--------------|
| 2020 | [PetteriM1](https://github.com/PetteriM1), [CreeperFace](https://github.com/Creeperface01) |
| 2019 | [PetteriM1](https://github.com/PetteriM1) |
| 2018 | [Jesse Boyd](https://github.com/boy0001), [Rutger Kok](https://github.com/rutgerkok) |
| 2017 | [Ankit](https://github.com/AnkitM252), [Langsdorf](https://github.com/Langsdorf), [MrPowerGamerBR](https://github.com/MrPowerGamerBR), [NycuRO](https://github.com/NycuRO), [PikyCZ](https://github.com/PikyCZ), [RaynLegends](https://github.com/RaynLegends), [Rutger Kok](https://github.com/rutgerkok), [SuperAlecs](https://github.com/SuperAlecs) |
| 2016 | [daveboden](https://github.com/daveboden), [Langsdorf](https://github.com/Langsdorf), [MrPowerGamerBR](https://github.com/MrPowerGamerBR), [OliverUv](https://github.com/OliverUv), [Rutger Kok](https://github.com/rutgerkok), [ZINGDING](https://github.com/ZINGDING) |

## License

Pokkit is licensed under the MIT license, except for the files derived from the Bukkit project. See [LICENSE.md](./LICENSE.md) for details.
