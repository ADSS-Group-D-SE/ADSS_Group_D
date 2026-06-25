## Team

| Name | ID |
|------|----|
| תום זקס | 325149268 |
| אופיר יוסף | 324077429 |
| אורי עטר | 322915133 |
| סנז'נה עמליה פטרקוב | 329561336 |


## How to run (release jar)

From the `release/` folder (it contains both the jar and the `database.db` it uses):

```bash
java -jar adss2025_v02.jar
```

On startup the program asks whether to load existing data from the database, then shows a
text menu to enter the Inventory module, the Supplier module, or clear the saved data.

> **Note:** the database schema lives inside `database.db`. Keep that file next to the jar
> (it is shipped in `release/`); the application opens `database.db` in the current working
> directory.

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
