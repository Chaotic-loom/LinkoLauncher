package com.chaotic_loom.util;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LogTail {
    private RandomAccessFile reader;
    private long filePointer;
    private final List<String> lines = new ArrayList<>();

    public LogTail(Path logPath) {
        try {
            reader = new RandomAccessFile(logPath.toFile(), "r");
            filePointer = 0;
            update();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update() {
        try {
            long len = reader.length();
            if (len < filePointer) {
                // Log was truncated or rotated—reset
                reader.seek(0);
                filePointer = 0;
                lines.clear();
            }
            if (len > filePointer) {
                reader.seek(filePointer);
                String raw;
                while ((raw = reader.readLine()) != null) {
                    // IMGUI expects UTF‑8; RandomAccessFile#readLine gives ISO‑8859‑1
                    lines.add(new String(raw.getBytes(StandardCharsets.ISO_8859_1),
                            StandardCharsets.UTF_8));
                }
                filePointer = reader.getFilePointer();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getLines() {
        return lines;
    }
}
