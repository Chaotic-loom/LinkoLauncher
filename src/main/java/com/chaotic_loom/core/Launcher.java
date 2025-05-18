package com.chaotic_loom.core;

import com.chaotic_loom.graphics.Renderer;
import com.chaotic_loom.scene.Scene;
import com.chaotic_loom.graphics.TextureManager;
import com.chaotic_loom.graphics.Window;
import com.chaotic_loom.registries.RegistryManager;
import com.chaotic_loom.input.InputManager;
import com.chaotic_loom.util.Loggers;
import com.chaotic_loom.util.OSDetector;

public class Launcher {
    private final Window window;
    private final Renderer renderer;
    private final TextureManager textureManager;
    private final Timer timer;
    private final InputManager inputManager;

    private final Scene scene;

    private final OSDetector.OS os;
    private final OSDetector.Distro distro;

    private static Launcher instance = new Launcher();

    public static Launcher getInstance() {
        return Launcher.instance;
    }

    public Launcher() {
        this.os = OSDetector.detectOS();
        this.distro = OSDetector.detectLinuxDistro();

        this.window = new Window("Linko");
        this.renderer = new Renderer();
        this.textureManager = new TextureManager();
        this.timer = new Timer();
        this.inputManager = new InputManager();

        this.scene = new Scene();
    }

    public void init() {
        Loggers.LAUNCHER.info("OS: {}, Distro: {}", os, distro);

        this.window.init();

        RegistryManager.init();
        textureManager.bakeAtlases("textures");

        this.timer.init();
        this.renderer.init();
        this.scene.init();
        this.inputManager.init(this.window);

        this.loop();

        this.inputManager.cleanup(this.window);
        this.renderer.cleanup();
        this.window.cleanup();
    }

    protected void loop() {
        while (!this.window.isCloseRequested()) {
            // Get time delta for this frame
            this.timer.updateElapsedTime();

            // --- Client Update ---
            this.inputManager.update();

            // --- Rendering ---
            this.scene.render(this.timer);
            this.timer.frameRendered(); // Update FPS counter

            // --- Window update ---
            if (!this.window.isVSync()) {
                sync(); // Syncing (Manual FPS Cap)
            }

            this.window.update();
        }
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

    public Window getWindow() {
        return window;
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public TextureManager getTextureManager() {
        return textureManager;
    }

    public Timer getTimer() {
        return timer;
    }

    public InputManager getInputManager() {
        return inputManager;
    }

    public OSDetector.OS getOs() {
        return os;
    }

    public OSDetector.Distro getDistro() {
        return distro;
    }
}
