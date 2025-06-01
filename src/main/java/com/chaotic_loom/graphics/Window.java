package com.chaotic_loom.graphics;

import com.chaotic_loom.core.Launcher;
import com.chaotic_loom.util.Loggers;
import com.chaotic_loom.events.WindowEvents;
import com.chaotic_loom.util.OSDetector;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private long windowHandle;

    private String title;
    private boolean vsync = true;

    private int width, height;

    public Window(String title) {
        this.title = title;
        this.width = 800;
        this.height = 480;
    }

    public void init() {
        // Setup an error callback.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW.
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        Loggers.WINDOW.info("Platform: {}", RenderPlatform.getRenderPlatform());

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        // Request OpenGL Core profile
        setUpGLFWContextVersion();
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

        //glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);

        // Obtain monitor and its video mode
        long monitor = glfwGetPrimaryMonitor();
        if (monitor == NULL) {
            throw new IllegalStateException("Primary monitor not found");
        }

        GLFWVidMode vidmode = glfwGetVideoMode(monitor);
        if (vidmode == null) {
            throw new IllegalStateException("Unable to query video mode");
        }

        this.updateSizeData(vidmode);

        // Create the fullscreen window
        windowHandle = glfwCreateWindow(this.width, this.height, title, monitor, NULL);
        if (windowHandle == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Setup resize callback
        glfwSetFramebufferSizeCallback(windowHandle, (window, w, h) -> {
            this.updateSizeData(vidmode);
            glViewport(0, 0, w, h);
            WindowEvents.RESIZE.invoker().onEvent(this, w, h);
        });

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(windowHandle, pWidth, pHeight);
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(windowHandle);

        // Enable v-sync if required
        setVSync(vsync);

        // Make the window visible
        glfwShowWindow(windowHandle);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();
        glViewport(0, 0, this.width, this.height);

        // Set the clear color (White)
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        Loggers.WINDOW.info("OpenGL Initialized: {}", glGetString(GL_VERSION));
    }

    public void setUpGLFWContextVersion() {
        OSDetector.OS os = Launcher.getInstance().getOs();
        OSDetector.Distro distro = Launcher.getInstance().getDistro();

        if (os == OSDetector.OS.LINUX && distro == OSDetector.Distro.LINKO) {
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
        } else {
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        }
    }

    public void update() {
        glfwSwapBuffers(windowHandle); // swap the color buffers
        glfwPollEvents(); // Poll for window events. The key callback will only be invoked during this call.
    }

    public void cleanup() {
        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(windowHandle);
        glfwDestroyWindow(windowHandle);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        GLFWErrorCallback callback = glfwSetErrorCallback(null);
        if (callback != null) {
            callback.free();
        }

        Loggers.WINDOW.info("Window cleaned up.");
    }

    public void updateSizeData(GLFWVidMode vidmode) {
        if (Launcher.getInstance().getOs() != OSDetector.OS.LINUX) {
            return;
        }

        if (Launcher.getInstance().getDistro() != OSDetector.Distro.LINKO) {
            return;
        }

        this.width = vidmode.width();
        this.height = vidmode.height();
    }

    public boolean isCloseRequested() {
        return glfwWindowShouldClose(windowHandle);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getTitle() {
        return title;
    }

    public long getWindowHandle() {
        return windowHandle;
    }

    public boolean isVSync() {
        return vsync;
    }

    public void setVSync(boolean vsync) {
        this.vsync = vsync;
        if (vsync) {
            glfwSwapInterval(1); // Enable VSync
        } else {
            glfwSwapInterval(0); // Disable VSync
        }
    }

    public float getAspectRatio() {
        return (float) this.width / this.height;
    }

    public enum RenderPlatform {
        ANY, WIN32, COCOA, WAYLAND, X11, NULL;

        public static RenderPlatform getRenderPlatform() {
            int platform = glfwGetPlatform();

            return switch (platform) {
                case GLFW_ANY_PLATFORM -> ANY;
                case GLFW_PLATFORM_WIN32 -> WIN32;
                case GLFW_PLATFORM_COCOA -> COCOA;
                case GLFW_PLATFORM_WAYLAND -> WAYLAND;
                case GLFW_PLATFORM_X11 -> X11;
                case GLFW_PLATFORM_NULL -> NULL;
                default -> throw new IllegalStateException("Unexpected platform: " + platform);
            };
        }
    }
}
