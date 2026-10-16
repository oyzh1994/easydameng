# EasyDameng

English | [简体中文](README.md)

EasyDameng is a JavaFX desktop client for Dameng databases. It provides connection management, database object maintenance, SQL querying, data import and export, data transport, and database dump capabilities.

Follow-up updates  
https://github.com/oyzh1994/easyshell  

## Features

- Manage multiple Dameng database connections and their configuration.
- Browse and maintain schemas, tables, views, indexes, foreign keys, triggers, functions, and procedures.
- Inspect table structures, object definitions, and database information, with common DDL operations.
- Query SQL and edit result sets, with query and code-generation tools.
- Import, export, and dump data in CSV, TXT, JSON, XML, Excel, and SQL formats.
- Transfer table structures, data, views, functions, procedures, and triggers between Dameng databases.
- Use an integrated JavaFX workspace with tabs, event bus, internationalization, themes, and terminal support.

## Project Structure

| Directory | Description |
|---|---|
| `cn.oyzh.easydameng.dameng` | Dameng client, SQL generation, metadata, queries, and database object models |
| `cn.oyzh.easydameng.data` | Data import, export, dump, and transport |
| `cn.oyzh.easydameng.controller` | Window and business controllers |
| `cn.oyzh.easydameng.fx` | JavaFX controls and UI adapters |
| `cn.oyzh.easydameng.tabs` | Main workspace, query, object design, and terminal tabs |
| `cn.oyzh.easydameng.store` | Local settings and persistence |
| `cn.oyzh.easydameng.terminal` | Terminal integration |

## Requirements

- JDK 25
- Maven 3.9 or newer
- Dameng JDBC driver `DmJdbcDriver18.jar`

## Build

Install the Dameng JDBC driver into the local Maven repository when it is not already available:

```bash
mvn install:install-file \
  -Dfile=DmJdbcDriver18.jar \
  -DgroupId=com.dameng \
  -DartifactId=DmJdbcDriver18 \
  -Dversion=8.1.3.140 \
  -Dpackaging=jar
```

Install dependency projects and build EasyDameng:

```bash
mvn clean install -DskipTests
```

For development, run either entry point from your IDE:

- `cn.oyzh.easydameng.EasyDamengApp`
- `cn.oyzh.easydameng.EasyDamengBootstrap`

Run tests with:

```bash
mvn test
```

## Related Projects

- [`base`](https://gitee.com/oyzh1994/base): shared utilities, events, internationalization, storage, SSH, packaging, and other foundations.
- [`fx-base`](https://gitee.com/oyzh1994/fx-base): JavaFX UI, database, terminal, rich-text, and related extensions.

## Notes

- Make sure `java -version` reports JDK 25; otherwise Maven may fail with `invalid target release: 25`.
- Import, dump, and transport operations may modify or overwrite the target database. Confirm the target environment and create backups before running them.
- Connection configuration and application settings are managed by the project storage modules. Protect local data that contains credentials.

## License

The project includes `APACHE-LICENSE-2.0.txt` and `MIT-LICENSE.txt`. Review the applicable terms before use.
