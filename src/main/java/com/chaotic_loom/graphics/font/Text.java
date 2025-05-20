package com.chaotic_loom.graphics.font;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.Objects;

public class Text {
    private FloatBuffer buffer;
    private String text;
    private final float spaceAdvance;

    public Text(String text) {
        Glyph spaceGlyph = Font.getGlyph(' ');
        spaceAdvance = (spaceGlyph != null) ? spaceGlyph.xAdvance : 10f; // Some fonts do not have spaces

        setText(text);
    }

    private void update() {
        buffer = MemoryUtil.memAllocFloat(text.length()*6*5);

        float penX = 0, penY = 0;
        for (char c : text.toCharArray()) {
            if (c == ' ') {
                penX += spaceAdvance;
                continue;
            }

            Glyph g = Font.getGlyph(c);

            if (g == null) g = Font.getGlyph('?');
            if (g == null) continue;

            float x0 = penX + g.xOffset;
            float y0 = penY + g.yOffset;
            float x1 = x0 + g.width;
            float y1 = y0 + g.height;

            // positions (x,y,z) + uvs
            Font.addQuad(buffer,
                    x0, y0,
                    x1, y1,
                    g.u0, g.v0, g.u1, g.v1
            );
            penX += g.xAdvance;
        }

        buffer.flip();
    }

    public void setText(String text) {
        if (Objects.equals(this.text, text)) {
            return;
        }

        this.text = text;
        update();
    }

    public String getText() {
        return text;
    }

    public FloatBuffer getBuffer() {
        return buffer;
    }
}
