package com.chaotic_loom.util;

import com.chaotic_loom.core.Launcher;
import com.chaotic_loom.graphics.Renderer;
import com.chaotic_loom.graphics.Window;
import com.chaotic_loom.registries.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Loggers {
    public static Logger LAUNCHER = LogManager.getLogger(Launcher.class.getSimpleName());
    public static Logger WINDOW = LogManager.getLogger(Window.class.getSimpleName());
    public static Logger RENDERER = LogManager.getLogger(Renderer.class.getSimpleName());
    public static Logger REGISTRY = LogManager.getLogger(Registry.class.getSimpleName());
    public static Logger TEXTURE_MANAGER = LogManager.getLogger(Renderer.class.getSimpleName());
    public static Logger INPUT = LogManager.getLogger(Renderer.class.getSimpleName());
}
