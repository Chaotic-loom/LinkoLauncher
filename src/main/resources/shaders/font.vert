#version 310 es

// Per‑vertex attributes: position in 3D and UV in atlas
layout(location = 0) in vec3 inPos;    // glyph quad vertex position
layout(location = 1) in vec2 inUV;     // corresponding texture coords

// Passed to fragment stage
out vec2 TexCoords;

// Uniforms for transforming into clip‑space
uniform mat4 model;    // your text’s model matrix
uniform mat4 viewMatrix;     // camera view matrix
uniform mat4 projectionMatrix;     // projection (perspective or ortho)

// Main entry
void main() {
    // 1) Transform to clip‑space
    gl_Position = projectionMatrix * viewMatrix * model * vec4(inPos, 1.0);
    // 2) Forward UVs
    TexCoords = inUV;
}
