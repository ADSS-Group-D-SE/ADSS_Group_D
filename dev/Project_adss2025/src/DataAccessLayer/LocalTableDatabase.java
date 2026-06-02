package DataAccessLayer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Small file-backed table store used to keep the assignment runnable with
 * java -jar and no external JDBC dependency.
 */
public class LocalTableDatabase {

    private Path databaseDirectory;

    public LocalTableDatabase(Path databaseDirectory) {
        this.databaseDirectory = databaseDirectory;
    }

    public boolean hasData() {
        return Files.exists(databaseDirectory.resolve("suppliers.tbl"));
    }

    public void clear() {
        try {
            if (!Files.exists(databaseDirectory)) {
                return;
            }
            try (java.util.stream.Stream<Path> paths = Files.list(databaseDirectory)) {
                for (Path path : paths.toArray(Path[]::new)) {
                    if (Files.isRegularFile(path) && path.getFileName().toString().endsWith(".tbl")) {
                        Files.delete(path);
                    }
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed clearing local database: " + e.getMessage(), e);
        }
    }

    public void writeTable(String tableName, List<String> columns, List<Map<String, String>> rows) {
        try {
            Files.createDirectories(databaseDirectory);
            List<String> lines = new ArrayList<>();
            lines.add(String.join("|", columns));
            for (Map<String, String> row : rows) {
                List<String> encodedValues = new ArrayList<>();
                for (String column : columns) {
                    encodedValues.add(encode(row.get(column)));
                }
                lines.add(String.join("|", encodedValues));
            }
            Files.write(databaseDirectory.resolve(tableName + ".tbl"), lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed writing table " + tableName + ": " + e.getMessage(), e);
        }
    }

    public List<Map<String, String>> readTable(String tableName) {
        Path tablePath = databaseDirectory.resolve(tableName + ".tbl");
        if (!Files.exists(tablePath)) {
            return new ArrayList<>();
        }
        try {
            List<String> lines = Files.readAllLines(tablePath, StandardCharsets.UTF_8);
            if (lines.isEmpty()) {
                return new ArrayList<>();
            }
            String[] columns = lines.get(0).split("\\|", -1);
            List<Map<String, String>> rows = new ArrayList<>();
            for (int i = 1; i < lines.size(); i++) {
                if (lines.get(i).isEmpty()) {
                    continue;
                }
                String[] values = lines.get(i).split("\\|", -1);
                Map<String, String> row = new LinkedHashMap<>();
                for (int j = 0; j < columns.length; j++) {
                    String value = j < values.length ? decode(values[j]) : "";
                    row.put(columns[j], value);
                }
                rows.add(row);
            }
            return rows;
        } catch (IOException e) {
            throw new IllegalStateException("Failed reading table " + tableName + ": " + e.getMessage(), e);
        }
    }

    private String encode(String value) {
        String safeValue = value == null ? "" : value;
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(safeValue.getBytes(StandardCharsets.UTF_8));
    }

    private String decode(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        return new String(Base64.getUrlDecoder().decode(value), StandardCharsets.UTF_8);
    }
}
