package com.chaotic_loom.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class OSHelper {
    public static void executeCommand(String command) {
        try {
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();

            // Leer la salida del comando
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                Loggers.LAUNCHER.info(line);
            }

            // Leer los errores del comando
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                Loggers.LAUNCHER.error(line);
            }
        } catch (Exception e) {
            Loggers.LAUNCHER.error(e);
        }
    }
}
