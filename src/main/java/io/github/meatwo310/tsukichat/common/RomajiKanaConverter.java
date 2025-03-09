package io.github.meatwo310.tsukichat.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RomajiKanaConverter {
    private static final Logger LOGGER = LoggerFactory.getLogger(RomajiKanaConverter.class);
    private static final String TABLE_PATH = "assets/%s/romaji_table.txt".formatted(Converter.MODID);

    static final Map<String, String> ROMAJI_TO_KANA = new HashMap<>();
    static final Map<String, Integer> ROMAJI_ROLLBACK = new HashMap<>();

    static {
        loadRomajiTable();
    }

    private static void loadRomajiTable() {
        InputStream is = RomajiKanaConverter.class.getClassLoader().getResourceAsStream(TABLE_PATH);
        if (is == null) {
            throw new TableNotFoundException("Romaji table not found at " + TABLE_PATH);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Skip empty lines and comments
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                // Analyze the line
                String[] parts = line.split(":");

                if (parts.length < 2) {
                    continue;
                }

                String key = parts[0];
                String value = parts[1];
                ROMAJI_TO_KANA.put(key, value);

                if (parts.length < 3) {
                    continue;
                }

                try {
                    int rollback = Integer.parseInt(parts[2]);
                    ROMAJI_ROLLBACK.put(key, rollback);
                } catch (NumberFormatException e) {
                    LOGGER.error("Illegal rollback value for key '{}': {}", key, parts[2], e);
                }
            }
            LOGGER.info(
                    "Successfully loaded Romaji table with {} entries and {} rollback entries",
                    ROMAJI_TO_KANA.size(),
                    ROMAJI_ROLLBACK.size()
            );
        } catch (IOException e) {
            throw new TableLoadingException("Failed to load Romaji table", e);
        }
    }

    private static class TableNotFoundException extends RuntimeException {
        public TableNotFoundException(String message) {
            super(message);
        }

        public TableNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private static class TableLoadingException extends RuntimeException {
        public TableLoadingException(String message) {
            super(message);
        }

        public TableLoadingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}