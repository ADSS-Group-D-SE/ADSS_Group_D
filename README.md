# ADSS – Group D

Automated Delivery & Supply System – Practical Assignment 2.
A single integrated system combining the **Inventory** module and the **Supplier** module
(catalog, suppliers, agreements, orders, automatic shortage orders and periodic buy orders),
backed by an SQLite database.

## Team

| Name | ID |
|------|----|
| תום זקס | 325149268 |
| אופיר יוסף | 324077429 |
| אורי עטר | 322915133 |
| סנז'נה עמליה פטרקוב | 329561336 |

## Requirements

- **Java 21 or newer** (the jar is built for Java 21; it was developed and tested on Temurin 25).
- No other installation needed — the SQLite driver and all dependencies are bundled inside the jar.

## How to run (release jar)

From the `release/` folder (it contains both the jar and the `database.db` it uses):

```bash
java -jar adss2025_v02.jar
```

On Java 22+ you may see a one‑line "restricted native access" warning from the SQLite driver.
It is harmless; to silence it run:

```bash
java --enable-native-access=ALL-UNNAMED -jar adss2025_v02.jar
```

On startup the program asks whether to load existing data from the database, then shows a
text menu to enter the Inventory module, the Supplier module, or clear the saved data.

> **Note:** the database schema lives inside `database.db`. Keep that file next to the jar
> (it is shipped in `release/`); the application opens `database.db` in the current working
> directory.

## Building from source

The project is a plain Java project (sources under `dev/Project_adss2025/src`).

**IntelliJ IDEA:** open `dev/Project_adss2025`, then run `src/Main.java`.

**Command line** (from `dev/Project_adss2025`, with the dependency jars in `lib/`):

```bash
# compile
javac -d out $(find src -name "*.java" ! -path "*/test/*")
# run
java -cp "out;lib/sqlite-jdbc-3.46.1.0.jar;lib/slf4j-api-1.7.36.jar;lib/slf4j-nop-1.7.36.jar" Main
```

(On Windows PowerShell the classpath separator is `;`; on Linux/macOS use `:`.)

## Running the tests

There are **35 unit & integration tests** under `dev/Project_adss2025/src/test/DomainLayerTests`
(`ProductFacadeTests`, `CategoryFacadeTests`, `SupplierFacadeTests`, `OrderFacadeTests`),
written with **JUnit 5**.

- **IntelliJ:** right‑click the `src/test` folder → *Run Tests*.
- **Command line** (using the JUnit console launcher in `lib/`):

```bash
java -jar lib/junit-platform-console-standalone-1.14.0.jar execute \
  -cp "out;lib/sqlite-jdbc-3.46.1.0.jar;lib/slf4j-api-1.7.36.jar;lib/slf4j-nop-1.7.36.jar" \
  --select-package=DomainLayerTests --details=tree
```

The tests run against the `database.db` in the working directory; to avoid changing the
seeded data, run them from a copy of that file.

## Tools & libraries

- **Java 21+**
- **SQLite** via `org.xerial:sqlite-jdbc` 3.46.1.0 (JDBC driver, bundled in the jar)
- `org.slf4j:slf4j-api` + `slf4j-nop` 1.7.36 (logging, required by the SQLite driver)
- **JUnit 5** (`junit-jupiter` 5.x) — tests only
- Diagrams (use cases, sequence, class diagram) were made with **draw.io**

## Project structure

```
dev/Project_adss2025/src/
  Main.java                      # entry point (module selection menu)
  CrossCuttingPackage/           # shared DTOs, Notification, Report, Response
  InventoryModule/               # Presentation / Service / Domain / DataAccess layers
  SupplierModule/                # Presentation / Service / Domain / DataAccess layers
  test/DomainLayerTests/         # JUnit 5 tests
release/                         # runnable jar + database.db
docs/                            # design artifacts (on the submission branch)
```
