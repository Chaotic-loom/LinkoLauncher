#version 330 core

in vec2 TexCoords;                 // from vertex shader
out vec4 FragColor;                // final fragment color

uniform sampler2D textureSampler;       // bound to unit 0
uniform vec4      tintColor;       // RGBA tint (alpha used as overall opacity)

void main() {
    float alpha = texture(textureSampler, TexCoords).r;
    FragColor = vec4(tintColor.rgb, tintColor.a * alpha);
}
