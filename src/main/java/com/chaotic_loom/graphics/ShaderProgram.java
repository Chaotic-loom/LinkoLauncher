package com.chaotic_loom.graphics;

import com.chaotic_loom.registries.RegistryObject;
import com.chaotic_loom.util.Loggers;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram extends RegistryObject {
    private final int programId;
    private int vertexShaderId;
    private int fragmentShaderId;

    private final Map<String, Integer> uniforms;

    public ShaderProgram() {
        programId = glCreateProgram();
        if (programId == 0) {
            throw new RuntimeException("Could not create Shader program");
        }
        uniforms = new HashMap<>();
    }

    public ShaderProgram(String vertexShader, String fragmentShader) {
        programId = glCreateProgram();
        if (programId == 0) {
            throw new RuntimeException("Could not create Shader program");
        }
        uniforms = new HashMap<>();

        createVertexShader(vertexShader);
        createFragmentShader(fragmentShader);
        link();
    }

    public ShaderProgram createVertexShader(String shaderCode) {
        return createVertexShaderFromPath(ShaderProgram.loadShaderResource(shaderCode));
    }

    public ShaderProgram createFragmentShader(String shaderCode) {
        return createFragmentShaderFromPath(ShaderProgram.loadShaderResource(shaderCode));
    }

    public ShaderProgram createVertexShaderFromPath(String shaderCode) {
        vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
        return this;
    }

    public ShaderProgram createFragmentShaderFromPath(String shaderCode)  {
        fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
        return this;
    }

    protected int createShader(String shaderCode, int shaderType) {
        int shaderId = glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new RuntimeException("Error creating shader. Type: " + shaderType);
        }

        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new RuntimeException("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024));
        }

        glAttachShader(programId, shaderId);

        return shaderId;
    }

    public void link() {
        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw new RuntimeException("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

        // Detach shaders after successful link - they are no longer needed
        if (vertexShaderId != 0) {
            glDetachShader(programId, vertexShaderId);
        }
        if (fragmentShaderId != 0) {
            glDetachShader(programId, fragmentShaderId);
        }

        // Validate program (debugging)
        glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            Loggers.RENDERER.error("Warning validating Shader code: {}", glGetProgramInfoLog(programId, 1024));
        }
    }

    public ShaderProgram createUniform(String... uniformNames) {
        for (String uniformName : uniformNames) {
            createUniform(uniformName);
        }

        return this;
    }

    public ShaderProgram createUniform(String uniformName) {
        int uniformLocation = glGetUniformLocation(programId, uniformName);
        if (uniformLocation < 0) {
            throw new RuntimeException("Could not find uniform:" + uniformName);
        }
        uniforms.put(uniformName, uniformLocation);

        return this;
    }


    // --- UNIFORM SETTERS ---

    public void setUniform(String uniformName, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = stack.mallocFloat(16);
            value.get(fb);
            glUniformMatrix4fv(uniforms.get(uniformName), false, fb);
        }
    }

    public void setUniform(String uniformName, int value) {
        glUniform1i(uniforms.get(uniformName), value);
    }

    public void setUniform(String uniformName, float value) {
        glUniform1f(uniforms.get(uniformName), value);
    }

    public void setUniform(String uniformName, Vector2f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = stack.mallocFloat(2);
            value.get(fb);
            glUniform2fv(uniforms.get(uniformName), fb);
        }
    }

    public void setUniform(String uniformName, Vector3f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = stack.mallocFloat(3);
            value.get(fb);
            glUniform3fv(uniforms.get(uniformName), fb);
        }
    }

    public void setUniform(String uniformName, Vector4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = stack.mallocFloat(4);
            value.get(fb);
            glUniform4fv(uniforms.get(uniformName), fb);
        }
    }


    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void cleanup() {
        unbind();
        if (programId != 0) {
            glDeleteProgram(programId);
        }
    }

    // --- Utility to load shader code from classpath ---
    public static String loadShaderResource(String resourcePath) {
        String result;

        try {
            try (InputStream in = ShaderProgram.class.getResourceAsStream(resourcePath);
                 java.util.Scanner scanner = new java.util.Scanner(in, StandardCharsets.UTF_8)) {
                result = scanner.useDelimiter("\\A").next();
            }
            if (result == null) {
                throw new RuntimeException("Could not load shader resource: " + resourcePath);
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }

        return result;
    }
}
