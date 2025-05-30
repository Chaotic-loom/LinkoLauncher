package com.chaotic_loom.util;

import com.chaotic_loom.core.Launcher;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class OSHelper {
    public static void reboot() {
        if (Launcher.getInstance().getOs() != OSDetector.OS.LINUX) {
            System.exit(0);
            return;
        }

        if (Launcher.getInstance().getDistro() != OSDetector.Distro.LINKO) {
            System.exit(0);
            return;
        }

        OSHelper.executeCommand("sudo reboot");
    }

    public static String executeCommand(String command) {
        if (Launcher.getInstance().getOs() != OSDetector.OS.LINUX) {
            return null;
        }

        if (Launcher.getInstance().getDistro() != OSDetector.Distro.LINKO) {
            return null;
        }

        StringBuilder result = new StringBuilder();

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
                result.append(line).append("\n");
            }
        } catch (Exception e) {
            Loggers.LAUNCHER.error(e);
            result.append(e.getMessage()).append("\n");
        }

        return result.toString();
    }
}
