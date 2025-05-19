package com.chaotic_loom.util;

import com.chaotic_loom.scene.Mesh;

/**
 * A simple 2D quad (single face) in the X-Y plane, centered at the origin.
 */
public class Quad {
    // Quad vertices (Pos: x, y, z)
    public static final float[] POSITIONS = {
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            0.5f,  0.5f, 0.0f,
            -0.5f,  0.5f, 0.0f
    };

    // Texture coordinates (UVs)
    public static final float[] BASE_UVS = {
            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f
    };

    // Normal vectors (all pointing +Z)
    public static final float[] NORMALS = {
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f
    };

    // Indices defining two triangles for the quad
    public static final int[] INDICES = {
            0, 1, 2,
            0, 2, 3
    };

    /**
     * Creates and returns a Mesh representing the quad.
     */
    public static Mesh createMesh() {
        return new Mesh(POSITIONS, BASE_UVS, NORMALS, INDICES, 100);
    }
}
