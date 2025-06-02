package com.chaotic_loom.core;

public class Main {
    public static void main(String[] args) {
        for (String arg : args) {
            if (arg.equals("crashed")) {
                CrashHandler.launchTerminal();
                return;
            }
        }

        Launcher.getInstance().init();
    }
}
