package com.chaotic_loom.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class UpdateManager {
    public static final String CURRENT_VERSION = FlagManager.readJarFlag("version");

    public static void update() {
        boolean isLatest = UpdateManager.isCurrentLatest();
        Loggers.LAUNCHER.info("Is latest version: {}", isLatest);

        if (isLatest) {
            return;
        }

        downloadFile();

        Loggers.LAUNCHER.info("Update downloaded!");
    }

    public static boolean isCurrentLatest() {
        String latest = fetchLatestRelease();

        Loggers.LAUNCHER.info("Latest launcher: {}", latest);
        Loggers.LAUNCHER.info("Current launcher: {}", CURRENT_VERSION);

        return Objects.equals(CURRENT_VERSION, latest);
    }

    public static void downloadFile() {
        String fileUrl = "https://chaotic-loom.com/download_latest";
        String savePath = "/opt/linko/launcher/";
        String fileName = "Update.jar";

        try {
            URL url = new URL(fileUrl);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            int responseCode = httpConn.getResponseCode();

            // Check if the path exists, if not, use the current working directory
            File directory = new File(savePath);
            if (!directory.exists()) {
                savePath = System.getProperty("user.dir") + File.separator;
            }

            // Create necessary directories if they don't exist
            new File(savePath).mkdirs();

            // Open input stream from the HTTP connection
            BufferedInputStream in = new BufferedInputStream(httpConn.getInputStream());
            FileOutputStream out = new FileOutputStream(savePath + fileName);

            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            out.close();
            in.close();

            Loggers.LAUNCHER.info("File downloaded successfully to: {}{}", savePath, fileName);
        } catch (IOException e) {
            Loggers.LAUNCHER.error(e);
        }
    }

    public static String fetchLatestRelease() {
        String urlString = "https://chaotic-loom.com/latest";
        StringBuilder response = new StringBuilder();

        try {
            URL url = new URL(urlString);
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(url.openConnection().getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }
        } catch (Exception e) {
            Loggers.LAUNCHER.error("Error fetching latest release:");
            Loggers.LAUNCHER.error(e);
            return null;
        }

        return response.toString().replace("LinkoLauncher-", "").replace(".jar", "");
    }
}
