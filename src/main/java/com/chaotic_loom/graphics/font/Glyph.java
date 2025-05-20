package com.chaotic_loom.graphics.font;

import com.chaotic_loom.scene.Texture;

public class Glyph {
    public final int id;
    float u0, v0, u1, v1;    // UV rectangle in atlas
    float xOffset, yOffset; // how the quad sits relative to pen position
    float width, height;    // size of the quad
    float xAdvance;         // how much to move pen after this glyph

    public Glyph(int id, float u0, float v0, float u1, float v1,
                 float xOffset, float yOffset,
                 float width, float height,
                 float xAdvance) {
        this.id = id;
        this.u0 = u0; this.v0 = v0;
        this.u1 = u1; this.v1 = v1;
        this.xOffset = xOffset; this.yOffset = yOffset;
        this.width = width; this.height = height;
        this.xAdvance = xAdvance;
    }

    public static Glyph fromFntLine(String line, Texture texture) {
        // expects tokens like: char id=65   x=34    y=45    width=20    height=30    xoffset=0     yoffset=5    xadvance=22    page=0  chnl=0
        String[] parts = line.trim().split("\\s+");
        int id=0; float x=0,y=0,w=0,h=0, xo=0, yo=0, xa=0;
        for (String p : parts) {
            String[] kv = p.split("=");
            switch (kv[0]) {
                case "id": id = Integer.parseInt(kv[1]); break;
                case "x": x = Float.parseFloat(kv[1]); break;
                case "y": y = Float.parseFloat(kv[1]); break;
                case "width": w = Float.parseFloat(kv[1]); break;
                case "height": h = Float.parseFloat(kv[1]); break;
                case "xoffset": xo = Float.parseFloat(kv[1]); break;
                case "yoffset": yo = Float.parseFloat(kv[1]); break;
                case "xadvance": xa = Float.parseFloat(kv[1]); break;
            }
        }
        // assume atlas size known or normalized later; here storing pixel coords
        float atlasW = texture.getWidth();
        float atlasH = texture.getHeight();
        float u0 = x / atlasW, v0 = y / atlasH;
        float u1 = (x + w) / atlasW;
        float v1 = (y + h) / atlasH;
        return new Glyph(id, u0, v0, u1, v1, xo, yo, w, h, xa);
    }
}
