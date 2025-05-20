package com.chaotic_loom.registries.built_in;

import com.chaotic_loom.core.Constants;
import com.chaotic_loom.graphics.ShaderProgram;
import com.chaotic_loom.registries.AbstractRegistryInitializer;
import com.chaotic_loom.registries.Identifier;
import com.chaotic_loom.registries.Registries;
import com.chaotic_loom.registries.Registry;

public class Shaders extends AbstractRegistryInitializer {
    public static ShaderProgram MAIN;
    public static ShaderProgram FONT;

    @Override
    public void register() {
        MAIN = Registry.register(
                Registries.SHADER,
                new Identifier(Constants.REGISTRY_NAMESPACE, "main"),
                new ShaderProgram("/shaders/main.vert", "/shaders/main.frag")
                        .createUniform("projectionMatrix", "viewMatrix", "tintColor", "textureSampler")
        );

        FONT = Registry.register(
                Registries.SHADER,
                new Identifier(Constants.REGISTRY_NAMESPACE, "font"),
                new ShaderProgram("/shaders/font.vert", "/shaders/font.frag")
                        .createUniform("projectionMatrix", "viewMatrix", "model", "tintColor", "textureSampler")
        );
    }
}
