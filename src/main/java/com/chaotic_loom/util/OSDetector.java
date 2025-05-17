package com.chaotic_loom.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility to detect the running OS and (for Linux) the specific distro.
 */
public final class OSDetector {

    public enum OS {
        WINDOWS, MAC, LINUX, SOLARIS, UNKNOWN
    }

    public enum Distro {
        LINKO, UBUNTU, DEBIAN, FEDORA, CENTOS, ARCH, GENERIC, UNKNOWN
    }

    /** Detect the high‑level OS. */
    public static OS detectOS() {
        String osName = System.getProperty("os.name", "generic").toLowerCase(Locale.ROOT);

        if (osName.contains("win")) {
            return OS.WINDOWS;
        } else if (osName.contains("mac") || osName.contains("darwin")) {
            return OS.MAC;
        } else if (osName.contains("nux") || osName.contains("nix")) {
            return OS.LINUX;
        } else if (osName.contains("sunos") || osName.contains("solaris")) {
            return OS.SOLARIS;
        } else {
            return OS.UNKNOWN;
        }
    }

    /** If on Linux, parse /etc/os-release to detect the distro. */
    public static Distro detectLinuxDistro() {
        if (detectOS() != OS.LINUX) {
            return Distro.UNKNOWN;
        }

        try {
            Map<String,String> info = Files.lines(Paths.get("/etc/os-release"))
                    .filter(line -> line.contains("="))
                    .map(line -> line.split("=", 2))
                    .collect(Collectors.toMap(
                            parts -> parts[0].trim(),
                            parts -> parts[1].replace("\"", "").trim()
                    ));

            String name = info.getOrDefault("ID", "").toLowerCase(Locale.ROOT);

            return switch (name) {
                case "linko" -> Distro.LINKO;
                case "ubuntu" -> Distro.UBUNTU;
                case "debian" -> Distro.DEBIAN;
                case "fedora" -> Distro.FEDORA;
                case "centos" -> Distro.CENTOS;
                case "arch" -> Distro.ARCH;
                default -> Distro.GENERIC;
            };
        } catch (IOException e) {
            // /etc/os-release not found or unreadable
            return Distro.UNKNOWN;
        }
    }

    public static void main(String[] args) {
        OS os = detectOS();
        System.out.println("Detected OS: " + os);

        if (os == OS.LINUX) {
            Distro distro = detectLinuxDistro();
            System.out.println("Detected Linux distro: " + distro);
        }
    }
}
