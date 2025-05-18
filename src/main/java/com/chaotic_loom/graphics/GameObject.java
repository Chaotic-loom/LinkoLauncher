package com.chaotic_loom.graphics;

import com.chaotic_loom.util.Transform;
import org.joml.Matrix4f;

public class GameObject {
    private final Transform transform;

    private final Mesh mesh;
    private final TextureAtlasInfo atlasInfo;

    // Cached model matrix
    private final Matrix4f modelMatrix;

    public GameObject(Mesh mesh, TextureAtlasInfo textureAtlasInfo) {
        this.transform = new Transform();
        this.mesh = mesh;
        this.atlasInfo = textureAtlasInfo;
        this.modelMatrix = new Matrix4f().identity();
        recalculateMatrix(); // Calculate initial matrix
    }

    // --- Getters ---
    public Mesh getMesh() {
        return mesh;
    }
    public TextureAtlasInfo getAtlasInfo() {
        return atlasInfo;
    }

    // --- Model Matrix ---
    private void recalculateMatrix() {
        modelMatrix.identity()
                .translate(this.getTransform().getPosition())
                .rotate(this.getTransform().getRotation())
                .scale(this.getTransform().getScale());

        this.getTransform().setClean();
    }

    public Matrix4f getModelMatrix() {
        if (this.getTransform().isDirty()) {
            recalculateMatrix();
        }
        return modelMatrix;
    }

    public Transform getTransform() {
        return transform;
    }
}
