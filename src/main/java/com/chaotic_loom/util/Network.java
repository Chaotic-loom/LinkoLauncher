package com.chaotic_loom.util;

import com.chaotic_loom.core.Launcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class Network {
    private static final long DEFAULT_TIMEOUT_MS = 30_000;

    /**
     * Connect to a new Wi-Fi network (WPA/WPA2).
     * This will create a new saved connection and immediately activate it.
     *
     * @param ssid     the network SSID
     * @param password the WPA/WPA2 passphrase
     * @throws IOException          if the nmcli command fails
     * @throws InterruptedException if the process is interrupted
     */
    public void connectNewWifi(String ssid, String password) throws IOException, InterruptedException {
        List<String> cmd = List.of(
                "nmcli", "dev", "wifi", "connect", "\"" + ssid + "\"",
                "password", "\"" + password + "\""
        );
        runCommand(cmd);
    }

    /**
     * Connect to an already‑known Wi‑Fi network by name.
     *
     * @param ssid the network SSID
     * @throws IOException          if the nmcli command fails
     * @throws InterruptedException if the process is interrupted
     */
    public void connectKnownWifi(String ssid) throws IOException, InterruptedException {
        List<String> cmd = List.of(
                "nmcli", "con", "up", "\"" + ssid + "\""
        );
        runCommand(cmd);
    }

    /**
     * List all saved (known) Wi‑Fi connections.
     *
     * @return a List of SSID strings
     * @throws IOException          if the nmcli command fails
     * @throws InterruptedException if the process is interrupted
     */
    public List<String> listKnownWifis() throws IOException, InterruptedException {
        List<String> cmd = List.of(
                "nmcli", "-t", "-f", "NAME,TYPE", "con", "show"
        );
        List<String> output = runCommand(cmd);
        List<String> wifis = new ArrayList<>();
        for (String line : output) {
            // lines look like "HomeWifi:wifi"
            String[] parts = line.split(":");
            if (parts.length == 2 && "wifi".equals(parts[1])) {
                wifis.add(parts[0]);
            }
        }
        return wifis;
    }

    /**
     * Remove (forget) a saved Wi‑Fi connection.
     *
     * @param ssid the network SSID to remove
     * @throws IOException          if the nmcli command fails
     * @throws InterruptedException if the process is interrupted
     */
    public void forgetWifi(String ssid) throws IOException, InterruptedException {
        List<String> cmd = List.of(
                "nmcli", "con", "delete", "\"" + ssid + "\""
        );
        runCommand(cmd);
    }

    /**
     * Check if any Wi‑Fi connection is currently active.
     *
     * @return true if connected, false otherwise
     * @throws IOException          if the nmcli command fails
     * @throws InterruptedException if the process is interrupted
     */
    public boolean isConnected() {
        try {
            Enumeration<NetworkInterface> ifs = NetworkInterface.getNetworkInterfaces();
            while (ifs.hasMoreElements()) {
                NetworkInterface nif = ifs.nextElement();
                if (nif.isLoopback()) {
                    // skip the loopback interface
                    continue;
                }
                if (nif.isUp()) {
                    // you’ve found a real, up interface
                    return true;
                }
            }
        } catch (Exception e) {
            Loggers.NETWORK.error("Error checking connectivity!");
            Loggers.NETWORK.error(e);
        }

        return false;
    }

    /**
     * Helper to run a command with a timeout, capture stdout, and throw on non-zero exit.
     *
     * @param command the command and its arguments
     * @return the stdout lines
     * @throws IOException          if starting or reading the process fails
     * @throws InterruptedException if the process is interrupted or times out
     */
    private List<String> runCommand(List<String> command)
            throws IOException, InterruptedException {
        if (Launcher.getInstance().getOs() != OSDetector.OS.LINUX) {
            return null;
        }

        if (Launcher.getInstance().getDistro() != OSDetector.Distro.LINKO) {
            return null;
        }

        ProcessBuilder pb = new ProcessBuilder(command);
        Process process = pb.start();

        // Reader for stdout
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getInputStream()));

        // Reader for stderr (in case of error output)
        BufferedReader errReader =
                new BufferedReader(new InputStreamReader(process.getErrorStream()));

        // Wait with timeout
        boolean finished = process.waitFor(DEFAULT_TIMEOUT_MS, java.util.concurrent.TimeUnit.MILLISECONDS);
        if (!finished) {
            process.destroyForcibly();
            throw new InterruptedException("Command timed out: " + String.join(" ", command));
        }

        int exitCode = process.exitValue();
        List<String> lines = new ArrayList<>();

        if (exitCode == 0) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } else {
            StringBuilder err = new StringBuilder();
            String line;
            while ((line = errReader.readLine()) != null) {
                err.append(line).append("\n");
            }
            throw new IOException(
                    "Command failed (" + exitCode + "): " +
                            String.join(" ", command) + "\n" + err
            );
        }
        return lines;
    }
}
