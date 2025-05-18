package com.chaotic_loom.registries;

import com.chaotic_loom.graphics.ShaderProgram;

import java.util.ArrayList;
import java.util.List;

public class Registries {
    // List of built-in keys for you to use, yippie!

    public static final RegistryKey<ShaderProgram> SHADER = new RegistryKey<>("shader");

    // Custom key system, where you can register your own keys

    private static final List<RegistryKey<?>> customKeys = new ArrayList<>();

    public static <T extends RegistryObject> RegistryKey<T> registerCustomKey(String id) {
        RegistryKey<T> customKey = new RegistryKey<>(id);
        customKeys.add(customKey);

        return customKey;
    }

    public static <T extends RegistryObject> RegistryKey<T> getCustomKey(String id) {
        for (RegistryKey<?> key : customKeys) {
            if (key.key().equals(id)) {
                RegistryKey<T> foundKey = (RegistryKey<T>) key;
                return foundKey;
            }
        }

        return null;
    }
}
