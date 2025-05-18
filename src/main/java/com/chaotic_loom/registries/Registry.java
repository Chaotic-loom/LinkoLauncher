package com.chaotic_loom.registries;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Registry {
    private static final Map<RegistryKey<?>, Map<Identifier, ?>> registries = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <T extends RegistryObject> T register(RegistryKey<T> registryKey, Identifier identifier, T object) {
        Map<Identifier, T> registry = getOrCreateRegistry(registryKey);

        identifier.setRegistryKey(registryKey);

        if (registry.containsKey(identifier)) {
            throw new IllegalArgumentException("Duplicate asset location: " + identifier);
        }

        object.setIdentifier(identifier);
        object.onPopulate();

        registry.put(identifier, object);

        return object;
    }

    public static <T extends RegistryObject> T getRegistryObject(RegistryKey<T> registryKey, Identifier identifier) {
        Map<Identifier, T> registry = getRegistry(registryKey);

        if (registry == null) return null;

        return registry.get(identifier);
    }

    @SuppressWarnings("unchecked")
    private static <T extends RegistryObject> Map<Identifier, T> getOrCreateRegistry(RegistryKey<T> registryKey) {
        return (Map<Identifier, T>) registries.computeIfAbsent(registryKey, k -> new HashMap<>());
    }

    @SuppressWarnings("unchecked")
    public static <T extends RegistryObject> Map<Identifier, T> getRegistry(RegistryKey<T> registryKey) {
        return (Map<Identifier, T>) registries.get(registryKey);
    }

    public static <T extends RegistryObject> boolean isNamespaceLoaded(String id) {
        for (Map.Entry<RegistryKey<?>, Map<Identifier, ?>> data : registries.entrySet()) {
            Map<Identifier, ?> map = data.getValue();

            for (Map.Entry<Identifier, ?> registryData : map.entrySet()) {
                Identifier identifier = registryData.getKey();

                if (Objects.equals(identifier.getNamespace(), id)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static Map<RegistryKey<?>, Map<Identifier, ?>> getRegistries() {
        return registries;
    }
}
