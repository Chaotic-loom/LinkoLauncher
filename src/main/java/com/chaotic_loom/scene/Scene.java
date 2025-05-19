package com.chaotic_loom.scene;

import com.chaotic_loom.core.Launcher;
import com.chaotic_loom.core.Timer;
import com.chaotic_loom.graphics.TextureAtlasInfo;
import com.chaotic_loom.util.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Scene {
    private final RenderStats renderStats;
    private final List<GameObject> gameObjects; // TEMP state
    private final Map<Texture, Map<Mesh, Map<TextureAtlasInfo, List<Matrix4f>>>> atlasRenderBatch;

    private final Camera camera;

    private GameObject superCoolCube, idk;

    public Scene() {
        this.camera = new Camera();
        this.renderStats = new RenderStats();
        this.gameObjects = new ArrayList<>();
        this.atlasRenderBatch = new HashMap<>();
    }

    public void init() {
        camera.setAspectRatio(Launcher.getInstance().getWindow().getAspectRatio());

        // Create sample geometry (TEMP)
        Mesh cubeMesh = Cube.createMesh();
        GameObject cube1 = new GameObject(cubeMesh, Launcher.getInstance().getTextureManager().getTextureInfo("/textures/stone.png"));
        cube1.getTransform().setPosition(0, 0, -2);
        gameObjects.add(cube1);
        GameObject cube2 = new GameObject(cubeMesh, Launcher.getInstance().getTextureManager().getTextureInfo("/textures/wood.png"));
        cube2.getTransform().setPosition(-1.5f, 0.5f, -3);
        cube2.getTransform().setScale(0.5f);
        gameObjects.add(cube2);
        GameObject cube3 = new GameObject(cubeMesh, Launcher.getInstance().getTextureManager().getTextureInfo("/textures/wood.png"));
        cube3.getTransform().setPosition(-3.5f, 1.5f, -4);
        cube3.getTransform().setScale(1.5f);
        gameObjects.add(cube3);

        for (int i = 0; i < 5; i++) {
            GameObject cube = new GameObject(cubeMesh, Launcher.getInstance().getTextureManager().getTextureInfo("/textures/dirt.png"));
            cube.getTransform().setPosition(0, 0, -4 * i);
            gameObjects.add(cube);
        }

        superCoolCube = cube1;
        idk = cube2;
    }

    private void prepareRenderBatch() {
        atlasRenderBatch.clear();
        renderStats.resetFrame();

        // Prepare batch
        for (GameObject go : gameObjects) {
            Mesh mesh = go.getMesh();
            TextureAtlasInfo atlasInfo = go.getAtlasInfo();

            // Validate necessary data
            if (mesh == null || atlasInfo == null || atlasInfo.atlasTexture() == null) {
                if (atlasInfo == null) Loggers.RENDERER.warn("GameObject missing AtlasInfo, skipping render.");
                continue;
            }

            Texture atlasTexture = atlasInfo.atlasTexture();

            // Populate the 3-level batch structure:
            atlasRenderBatch
                    .computeIfAbsent(atlasTexture, k -> new HashMap<>())    // Level 1: Atlas Texture
                    .computeIfAbsent(mesh, k -> new HashMap<>())    // Level 2: Mesh
                    .computeIfAbsent(atlasInfo, k -> new ArrayList<>())     // Level 3: AtlasInfo (UV region)
                    .add(go.getModelMatrix());      // Add instance matrix to the list
        }
    }

    public void render(Timer timer) {
        tempInput(timer);

        prepareRenderBatch();

        Launcher.getInstance().getRenderer().render(camera, atlasRenderBatch, renderStats);

        if (renderStats.getTotalFrames() % (60 * 10) == 0) {
            Loggers.RENDERER.info(renderStats.getSummary());
        }
    }

    private void tempInput(Timer timer) {
        superCoolCube.getTransform().setScale((float) Math.sin(timer.getTime()), (float) Math.cos(timer.getTime()), (float) Math.sin(timer.getTime()));
        superCoolCube.getTransform().rotateAround(new Vector3f(), timer.getDeltaTime() * 50, 0, 1, 0);

        idk.getTransform().rotateAround(superCoolCube.getTransform().getPosition(), timer.getDeltaTime() * 1000, 1, 1, 1, true);

        camera.getTransform().rotateAround(new Vector3f(), timer.getDeltaTime() * 10, (float) Math.sin(timer.getTime()), 1, (float) Math.cos(timer.getTime()), true);
        /*camera.getTransform().setPosition(
                (float) Math.sin(timer.getTime()),
                (float) Math.sin(timer.getTime()),
                3 + (float) Math.sin(timer.getTime())
        );*/

        if (Launcher.getInstance().getInputManager().isKeyPressed(GLFW.GLFW_KEY_R)) {
            OSHelper.executeCommand("sudo reboot");
        }

        if (Launcher.getInstance().getInputManager().isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
            System.exit(0);
        }
    }
}
