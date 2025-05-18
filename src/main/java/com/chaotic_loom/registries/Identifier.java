package com.chaotic_loom.registries;

import com.chaotic_loom.core.Constants;

import java.util.Objects;

public class Identifier {
    private String namespace;
    private String path;
    private RegistryKey<?> registryKey;

    public Identifier(String namespace, String path) {
        setUp(namespace, path);
    }

    public Identifier(String compressed) {
        if (!compressed.contains(":")) {
            throw new RuntimeException("Illegal compressed identifier. It should be like: \"namespace:path\"; But we found: " + compressed);
        }

        String[] parts = compressed.split(":");

        if (parts.length < 2) {
            throw new RuntimeException("Illegal compressed identifier. It is missing a part. It should be like: \"namespace:path\"; But we found: " + compressed);
        }

        setUp(parts[0], parts[1]);
    }

    private void setUp(String namespace, String path) {
        this.namespace = namespace;
        this.path = path;

        if (!isValidNamespace(namespace)) {
            throw new RuntimeException("Illegal namespace character in: " + this);
        }

        if (!isValidNPath(namespace)) {
            throw new RuntimeException("Illegal path character in: " + this);
        }
    }

    public static boolean isValidNamespace(String string) {
        return isValidString(string, Constants.VALID_NAMESPACE_CHARS);
    }

    public static boolean isValidNPath(String string) {
        return isValidString(string, Constants.VALID_PATH_CHARS);
    }

    private static boolean isValidString(String string, String validChars) {
        for (int i = 0; i < string.length(); ++i) {
            if (validChars.indexOf(string.charAt(i)) == -1) {
                return false;
            }
        }
        return true;
    }

    public String toString() {
        return this.namespace + ":" + this.path;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Identifier that = (Identifier) obj;
        return namespace.equals(that.namespace) && path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, path);
    }

    public String getNamespace() {
        return namespace;
    }

    public String getPath() {
        return path;
    }

    public void setRegistryKey(RegistryKey<?> registryKey) {
        this.registryKey = registryKey;
    }

    public RegistryKey<?> getRegistryKey() {
        return this.registryKey;
    }
}
