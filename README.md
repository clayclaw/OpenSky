# OpenSky
OpenSky is an open source plugin for island-style gameplay experience, with the ability to scale across multiple servers.
Please note that the plugin is still work-in-progress, do not use in production environment.

How it works:

OpenSky does not save island data, including world data in local files.
Instead, this plugin store data in MySQL database.
Redis cache is used for better query performance across multiple servers to reduce the amount of database query.

For now, this plugin rely on [Advanced Slime Paper](https://github.com/InfernalSuite/AdvancedSlimePaper/)
for world data handling.

## Getting Started
Prerequisites:
- [MySQL Database](https://www.mysql.com/)
- [Redis](https://redis.io/)
- [Advanced Slime Paper](https://github.com/InfernalSuite/AdvancedSlimePaper/) / [Advanced Slime Purpur](https://github.com/InfernalSuite/AdvancedSlimePurpur/)
  (Theoretically OpenSky plugin should support most version that uses Java 17+, but we only test with latest version of AdvancedSlimePaper)

You must use [Advanced Slime Paper](https://github.com/InfernalSuite/AdvancedSlimePaper/)
or [Advanced Slime Purpur](https://github.com/InfernalSuite/AdvancedSlimePurpur/).
Learn how to install the paper version at [here](https://github.com/InfernalSuite/AdvancedSlimePaper/blob/main/.docs/usage/install.md).

Once you have all these things configured, simply download OpenSky plugin from [here](https://http.cat/404) and configure it accordingly.
See [Installation Guide](https://http.cat/404) for more details.

## Features, Commands, Configurations, and more
See [Wiki](https://http.cat/404) for more details.
