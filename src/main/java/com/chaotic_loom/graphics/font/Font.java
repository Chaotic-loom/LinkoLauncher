package com.chaotic_loom.graphics.font;

import com.chaotic_loom.registries.built_in.Shaders;
import com.chaotic_loom.scene.Camera;
import com.chaotic_loom.scene.Texture;
import com.chaotic_loom.util.Loggers;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Font {
    private final Texture texture;
    private static final Map<Character, Glyph> glyphs = new HashMap<>();

    private int vao;
    private int vbo;

    public Font(String name) {
        String imagePath = name + ".png";
        String dataPath = name + ".fnt";

        this.texture = new Texture();
        glBindTexture(GL_TEXTURE_2D, this.texture.getTextureId());

        try {
            this.texture.loadFromClasspath2(imagePath);
        } catch (Exception e) {
            Loggers.TEXTURE_MANAGER.error("Could not load font texture");
            Loggers.TEXTURE_MANAGER.error(e);
        }

        loadGlyphData(dataPath);
        setupMesh();

        texture.setWidth(1024);
        texture.setHeight(1024);

        System.out.println("FONT TEXTURE DATA");
        System.out.println(texture.getTextureId());
    }

    private void loadGlyphData(String dataPath) {
        try (InputStream source = Font.class.getResourceAsStream(dataPath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(source)))
        {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("char id")) {
                    Glyph g = Glyph.fromFntLine(line, texture);
                    glyphs.put((char) g.id, g);
                }
            }
        } catch (Exception e) {
            Loggers.TEXTURE_MANAGER.error("Could not load font data");
            Loggers.TEXTURE_MANAGER.error(e);
            e.printStackTrace();
        }
    }

    private void setupMesh() {
        vao = glGenVertexArrays();
        vbo = glGenBuffers();

        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * Float.BYTES, 0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void renderText(Camera camera, Text text, float x, float y, float z,
                           float rotX, float rotY, float rotZ, float scale) {
        FloatBuffer buffer = text.getBuffer();

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_DYNAMIC_DRAW);

        // Setup shader uniforms
        Matrix4f model = new Matrix4f()
                .translate(x, y, z)
                .rotateX(rotX).rotateY(rotY).rotateZ(rotZ)
                .scale(scale);

        // assume a Shader.textShader exists
        Shaders.FONT.bind();
        Shaders.FONT.setUniform("projectionMatrix", camera.getProjectionMatrix());
        Shaders.FONT.setUniform("viewMatrix", camera.getViewMatrix());
        Shaders.FONT.setUniform("model", model);
        Shaders.FONT.setUniform("tintColor", new Vector4f(0,0,0,1));
        Shaders.FONT.setUniform("textureSampler", 0);

        // Draw
        texture.bind(0);
        glBindVertexArray(vao);
        glDrawArrays(GL_TRIANGLES, 0, buffer.limit() / 5);
        glBindVertexArray(0);
    }

    public static void addQuad(FloatBuffer buf,
                         float x0, float y0, float x1, float y1,
                         float u0, float v0, float u1, float v1) {
        // Flip Y
        float ny0 = -y0;
        float ny1 = -y1;

        // Triangle 1
        buf.put(x0).put(ny0).put(0).put(u0).put(v0);   // v0
        buf.put(x1).put(ny1).put(0).put(u1).put(v1);   // v2
        buf.put(x1).put(ny0).put(0).put(u1).put(v0);   // v1

        // Triangle 2
        buf.put(x1).put(ny1).put(0).put(u1).put(v1);   // v2
        buf.put(x0).put(ny0).put(0).put(u0).put(v0);   // v0
        buf.put(x0).put(ny1).put(0).put(u0).put(v1);   // v3
    }

    public static Glyph getGlyph(char c) {
        return glyphs.get(c);
    }
}
