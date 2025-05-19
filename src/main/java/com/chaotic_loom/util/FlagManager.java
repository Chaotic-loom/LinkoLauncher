package com.chaotic_loom.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FlagManager {
    public static final Path BOOT_FLAGS = Paths.get("/boot/");

    public static boolean flagExists(Path dir, String flag) {
        return Files.exists(dir.resolve(flag)) && Files.isRegularFile(dir.resolve(flag));
    }

    public static boolean deleteFlag(Path dir, String flag) {
        try {
            return Files.deleteIfExists(dir.resolve(flag));
        } catch (Exception e) {
            Loggers.LAUNCHER.error(e);
            return false;
        }
    }

    public static String readJarFlag(String flag) {
        try (InputStream is = UpdateManager.class.getClassLoader().getResourceAsStream("extra/" + flag)) {
            if (is == null) {
                Loggers.LAUNCHER.error("Resource 'extra/' {} not found in JAR.", flag);
                return null;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                return sb.toString().trim();
            }
        } catch (IOException e) {
            Loggers.LAUNCHER.error("Error reading {} resource: {}", flag, e.getMessage());
            return null;
        }
    }
}
