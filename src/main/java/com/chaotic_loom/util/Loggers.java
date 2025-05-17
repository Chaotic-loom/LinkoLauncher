package com.chaotic_loom.util;

import com.chaotic_loom.core.Launcher;
import com.chaotic_loom.graphics.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Loggers {
    public static Logger LAUNCHER = LogManager.getLogger(Launcher.class.getSimpleName());
    public static Logger WINDOW = LogManager.getLogger(Window.class.getSimpleName());
}
