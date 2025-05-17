package com.chaotic_loom.core;

import org.lwjgl.glfw.GLFW;

public class Timer {
    private double lastLoopTime;
    private int fpsCount;
    private double lastFpsTime;

    public void init() {
        lastLoopTime = getTime();
        lastFpsTime = getTime();
        fpsCount = 0;
    }

    public double getTime() {
        return GLFW.glfwGetTime();
    }

    public float getElapsedTime() {
        double currentTime = getTime();
        float elapsedTime = (float) (currentTime - lastLoopTime);
        lastLoopTime = currentTime;

        // Update FPS counter display each second (called by frameRendered)
        updateFPSCounter();

        return elapsedTime;
    }

    // Called externally after a frame is rendered
    public void frameRendered() {
        fpsCount++;
    }

    private void updateFPSCounter() {
        // Check only when a frame is rendered
        if (getTime() - lastFpsTime >= 1.0) {
            fpsCount = 0;
            lastFpsTime += 1.0; // Prevent drift
        }
    }

    public int getFpsCount() {
        return fpsCount;
    }
}
