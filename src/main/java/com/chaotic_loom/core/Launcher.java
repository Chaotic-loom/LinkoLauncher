package com.chaotic_loom.core;

import com.chaotic_loom.graphics.Window;
import com.chaotic_loom.util.Loggers;
import com.chaotic_loom.util.OSDetector;

import java.util.Random;

import static org.lwjgl.opengl.GL11.*;

public class Launcher {
    private Window window;
    private Timer timer;

    private OSDetector.OS os;
    private OSDetector.Distro distro;

    private float r = 0f, g = 0f, b = 0f;
    private double lastColorChangeTime = 0;
    private static final double COLOR_CHANGE_INTERVAL = 3.0; // seconds
    private Random random = new Random();

    public Launcher() {
        this.os = OSDetector.detectOS();
        this.distro = OSDetector.detectLinuxDistro();

        this.window = new Window("Linko");
        this.timer = new Timer();

        lastColorChangeTime = timer.getTime();
    }

    public void init() {
        Loggers.LAUNCHER.info("OS: {}, Distro: {}", os, distro);

        this.window.init();
        this.timer.init();

        this.loop();

        this.window.cleanup();
    }

    protected void loop() {
        float elapsedTime;

        while (!this.window.isCloseRequested()) {
            // Get time delta for this frame
            elapsedTime = this.timer.getElapsedTime();

            // --- Input Processing ---
            // Process continuous input (movement keys)
            //processInput(elapsedTime);

            // --- Client Update ---
            //inputManager.update();

            // --- Rendering ---
            render();
            this.timer.frameRendered(); // Update FPS counter

            // --- Window update ---
            if (!this.window.isVSync()) {
                sync(); // Syncing (Manual FPS Cap)
            }

            this.window.update();
        }
    }

    private void render() {
        double currentTime = timer.getTime();
        if (currentTime - lastColorChangeTime >= COLOR_CHANGE_INTERVAL) {
            r = random.nextFloat();
            g = random.nextFloat();
            b = random.nextFloat();
            lastColorChangeTime = currentTime;
        }

        glClearColor(r, g, b, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    private void sync() {
        float loopSlot = 1f / Constants.TARGET_FPS;
        double targetTime = lastSyncTime + loopSlot;

        while (this.timer.getTime() < targetTime - 0.001) { // Sleep until slightly before target
            try { Thread.sleep(1); } catch (InterruptedException ignore) {Thread.currentThread().interrupt(); break;}
        }
        // Busy wait for the last moment for higher precision
        // while (timer.getTime() < targetTime) { Thread.yield(); }

        lastSyncTime = this.timer.getTime(); // Track when sync finished
    }
    private double lastSyncTime = 0;
}
