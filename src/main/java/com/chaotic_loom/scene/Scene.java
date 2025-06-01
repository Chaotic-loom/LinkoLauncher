package com.chaotic_loom.scene;

import com.chaotic_loom.core.Launcher;
import com.chaotic_loom.core.Timer;
import com.chaotic_loom.graphics.TextureAtlasInfo;
import com.chaotic_loom.graphics.Window;
import com.chaotic_loom.graphics.font.Font;
import com.chaotic_loom.graphics.font.Text;
import com.chaotic_loom.util.*;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;


public class Scene {
    private final RenderStats renderStats;
    private final List<GameObject> gameObjects; // TEMP state
    private final List<GameObject> guis; // TEMP state
    private final Map<Texture, Map<Mesh, Map<TextureAtlasInfo, List<Matrix4f>>>> atlasRenderBatch;
    private final Map<Texture, Map<Mesh, Map<TextureAtlasInfo, List<Matrix4f>>>> atlasGuiRenderBatch;

    private final Camera camera;
    private final Camera guiCamera;

    private GameObject superCoolCube, idk, center;

    private Font font;
    private Text ssidText;
    private Text passText;

    public Scene() {
        this.camera = new Camera();
        this.guiCamera = new Camera();
        this.renderStats = new RenderStats();
        this.gameObjects = new ArrayList<>();
        this.guis = new ArrayList<>();
        this.atlasRenderBatch = new HashMap<>();
        this.atlasGuiRenderBatch = new HashMap<>();
    }

    public void init() {
        camera.setAspectRatio(Launcher.getInstance().getWindow().getAspectRatio());
        guiCamera.setAspectRatio(Launcher.getInstance().getWindow().getAspectRatio());

        int w = Launcher.getInstance().getWindow().getWidth();
        int h = Launcher.getInstance().getWindow().getHeight();

        float zNear = guiCamera.getNearPlane();
        float zFar = guiCamera.getFarPlane();

        float halfW = w * 0.5f;
        float halfH = h * 0.5f;

        guiCamera.setOrthographic(
                -halfW, halfW,
                -halfH, halfH,
                zNear,  zFar
        );


        GameObject frame = new GameObject(Quad.createMesh(), Launcher.getInstance().getTextureManager().getTextureInfo("/textures/icon.png"));
        int iw = 312 / 3;
        int ih = 386 / 3;
        frame.getTransform().setScale(iw, ih, 1);
        frame.getTransform().setPosition((float) -w/2 + (float) iw/2, (float) h/2 - (float) ih/2, 0);
        guis.add(frame);

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

            if (i == 0) {
                center = cube;
            }
        }

        font = new Font("/fonts/arial");
        ssidText = new Text("Command");
        passText = new Text("Result");

        superCoolCube = cube1;
        idk = cube2;
    }

    private void prepareRenderBatch(Map<Texture, Map<Mesh, Map<TextureAtlasInfo, List<Matrix4f>>>> batch, List<GameObject> gameObjects) {
        batch.clear();
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
            batch
                .computeIfAbsent(atlasTexture, k -> new HashMap<>())    // Level 1: Atlas Texture
                .computeIfAbsent(mesh, k -> new HashMap<>())    // Level 2: Mesh
                .computeIfAbsent(atlasInfo, k -> new ArrayList<>())     // Level 3: AtlasInfo (UV region)
                .add(go.getModelMatrix());      // Add instance matrix to the list
        }
    }

    public void render(Timer timer) {
        tempInput(timer);

        prepareRenderBatch(atlasRenderBatch, gameObjects);
        prepareRenderBatch(atlasGuiRenderBatch, guis);

        Launcher.getInstance().getRenderer().clear();
        glEnable(GL_DEPTH_TEST);
        Launcher.getInstance().getRenderer().render(camera, atlasRenderBatch, renderStats);
        glDisable(GL_DEPTH_TEST);
        Launcher.getInstance().getRenderer().render(guiCamera, atlasGuiRenderBatch, renderStats);

        Window window = Launcher.getInstance().getWindow();

        //glEnable(GL_BLEND);
        //glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        font.renderText(guiCamera, ssidText, -window.getWidth() / 2, window.getHeight() / 2, 0, 0, 0, 0, 1f);
        font.renderText(guiCamera, passText, -window.getWidth() / 2, window.getHeight() / 2 - 25, 0, 0, 0, 0, 1f);
        //glDisable(GL_BLEND);

        if (renderStats.getTotalFrames() % (60 * 10) == 0) {
            Loggers.RENDERER.info(renderStats.getSummary());
        }
    }

    private void tempInput(Timer timer) {
        superCoolCube.getTransform().setScale((float) Math.sin(timer.getTime()), (float) Math.cos(timer.getTime()), (float) Math.sin(timer.getTime()));
        superCoolCube.getTransform().rotateAround(new Vector3f(), timer.getDeltaTime() * 50, 0, 1, 0);

        idk.getTransform().rotateAround(center.getTransform().getPosition(), timer.getDeltaTime() * 1000, 1, 1, 1, true);

        center.getTransform().setPosition(0, (float) Math.cos(timer.getTime()), 0);

        camera.getTransform().rotateAround(new Vector3f(), timer.getDeltaTime() * 10, (float) Math.sin(timer.getTime()), 1, (float) Math.cos(timer.getTime()), true);
        /*camera.getTransform().setPosition(
                (float) Math.sin(timer.getTime()),
                (float) Math.sin(timer.getTime()),
                3 + (float) Math.sin(timer.getTime())
        );*/

        if (Launcher.getInstance().getInputManager().isKeyPressed(GLFW.GLFW_KEY_R)) {
            OSHelper.reboot();
        }

        if (Launcher.getInstance().getInputManager().isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
            OSHelper.executeCommand("sudo systemctl stop kiosk.service");
            System.exit(0);
        }

        if (Launcher.getInstance().getInputManager().isKeyPressed(GLFW.GLFW_KEY_U)) {
            throw new RuntimeException();
        }

        if (Launcher.getInstance().getInputManager().isKeyPressed(GLFW.GLFW_KEY_T) && !Launcher.getInstance().getInputManager().isTypingMode()) {
            Launcher.getInstance().getInputManager().startTextInput(Launcher.getInstance().getWindow().getWindowHandle());
        }

        String newText = Launcher.getInstance().getInputManager().getTypedText().toString();
        if (!newText.isBlank()) {
            ssidText.setText(newText);
        }

        if (Launcher.getInstance().getInputManager().isKeyPressed(GLFW.GLFW_KEY_ENTER) && Launcher.getInstance().getInputManager().isTypingMode()) {
            String text = Launcher.getInstance().getInputManager().stopTextInput(Launcher.getInstance().getWindow().getWindowHandle());

            ssidText.setText(text);

            String result = OSHelper.executeCommand(text);
            passText.setText(result);
        }
    }
}
