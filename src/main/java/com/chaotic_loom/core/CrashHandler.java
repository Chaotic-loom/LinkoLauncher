package com.chaotic_loom.core;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import com.chaotic_loom.graphics.Window;
import com.chaotic_loom.util.Loggers;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.GL_SHADING_LANGUAGE_VERSION;

public class CrashHandler {
    private static final Path CRASH_DIR = Paths.get("crash-reports");
    private static boolean hasCrashed = false;

    public static void handleCrash(String contextMessage, Throwable t) {
        hasCrashed = true;

        try {
            // 1. Ensure crash-reports directory exists
            if (!Files.exists(CRASH_DIR)) {
                Files.createDirectories(CRASH_DIR);
            }

            // 2. Build a timestamped filename
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String filename = "crash-" + timestamp + ".txt";
            Path crashFile = CRASH_DIR.resolve(filename);

            try (BufferedWriter out = Files.newBufferedWriter(crashFile, StandardCharsets.UTF_8)) {
                // 3. Write header
                out.write("---- Crash Report ----\n");
                out.write("Time: " + timestamp + "\n");
                out.write("Context: " + contextMessage + "\n");
                out.write("Exception name: " + t.getClass().getSimpleName() + "\n\n");

                // 4. JVM / OS info
                out.write("=== System Information ===\n");
                out.write("Java Version: " + System.getProperty("java.version") + "\n");
                out.write("Java Vendor: " + System.getProperty("java.vendor") + "\n");
                out.write("OS Name: " + System.getProperty("os.name") + "\n");
                out.write("OS Version: " + System.getProperty("os.version") + "\n");
                out.write("User: " + System.getProperty("user.name") + "\n\n");

                // 5. OpenGL / GPU info (if context still exists)
                out.write("=== OpenGL Information ===\n");
                try {
                    if (GLFW.glfwInit()) {
                        Window window = Launcher.getInstance().getWindow();

                        out.write("Renderer: " + window.glfw_GL_RENDERER() + "\n");
                        out.write("Vendor:   " + window.glfw_GL_VENDOR() + "\n");
                        out.write("Version:  " + window.glfw_GL_VERSION() + "\n");
                        out.write("GLSL:     " + window.glfw_GL_SHADING_LANGUAGE_VERSION() + "\n");
                    } else {
                        out.write("Could not initialize GLFW for GL info.\n");
                    }
                } catch (Throwable glInfoErr) {
                    out.write("Error retrieving OpenGL info: " + glInfoErr.getMessage() + "\n");
                }
                out.write("\n");

                // 6. Exception stack trace
                out.write("=== Stack Trace ===\n");
                StringWriter sw = new StringWriter();
                t.printStackTrace(new PrintWriter(sw));
                out.write(sw.toString());
                out.write("\n");

                // 7. Optionally gather more data (e.g. loaded mods, config, memory)
                out.write("=== Additional Data ===\n");
                out.write("Max Memory: " + Runtime.getRuntime().maxMemory() + "\n");
                out.write("Total Memory: " + Runtime.getRuntime().totalMemory() + "\n");
                out.write("Free Memory: " + Runtime.getRuntime().freeMemory() + "\n");

                out.flush();
            }

            // 8. Print a short message to console
            Loggers.CRASH_HANDLER.error("A fatal error occurred: {}", contextMessage);
            Loggers.CRASH_HANDLER.error("A crash report has been saved to: {}", crashFile.toAbsolutePath());
        } catch (IOException ioEx) {
            Loggers.CRASH_HANDLER.error("Failed to write crash report: {}", ioEx.getMessage());
            Loggers.CRASH_HANDLER.error(t);
            throw new RuntimeException(t);
        }

        Launcher.getInstance().getWindow().cleanup();

        //new Thread(CrashHandler::launchTerminal, "Crash handler terminal").start();
        launchTerminal();
    }

    private static void launchTerminal() {
        try {
            // 1) Create a Terminal and Screen
            DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
            Terminal terminal = terminalFactory.createTerminal();
            Screen screen = new TerminalScreen(terminal);
            screen.startScreen();

            // 2) Create the top‐level GUI and specify that windows should be centered
            MultiWindowTextGUI gui = new MultiWindowTextGUI(
                    screen,
                    new DefaultWindowManager(),
                    new EmptySpace(TextColor.ANSI.RED_BRIGHT) // background fill
            );

            // 3) Create a new window with a title
            BasicWindow window = new BasicWindow("Crash handler");
            window.setHints(Arrays.asList(com.googlecode.lanterna.gui2.Window.Hint.CENTERED));

            // 4) Build the content panel using a vertical layout
            Panel rootPanel = new Panel();
            rootPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

            // 4a) Title label
            Label title = new Label("=== Options ===")
                    .setForegroundColor(TextColor.ANSI.YELLOW)
                    .setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
            rootPanel.addComponent(title);

            // 4b) Horizontal separator
            Separator separator = new Separator(Direction.HORIZONTAL);
            rootPanel.addComponent(separator);

            // 4c) Three vertical “option” buttons
            // Each button’s Runnable will be executed when you press Enter or click it
            Panel optionsPanel = new Panel();
            optionsPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
            optionsPanel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));

            Button opt1 = new Button("Option 1", () -> {
                // ← Code to run when “Option 1” is selected:
                // For demo, just print to the terminal & close the menu
                System.out.println("You chose Option 1!");
                gui.getActiveWindow().close();
            });
            Button opt2 = new Button("Option 2", () -> {
                System.out.println("You chose Option 2!");
                gui.getActiveWindow().close();
            });
            Button opt3 = new Button("Option 3", () -> {
                System.out.println("You chose Option 3!");
                gui.getActiveWindow().close();
            });

            // Center each option button horizontally
            opt1.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
            opt2.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
            opt3.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
            optionsPanel.addComponent(opt1);
            optionsPanel.addComponent(opt2);
            optionsPanel.addComponent(opt3);

            rootPanel.addComponent(optionsPanel);

            // 4d) Add some space before the bottom buttons
            rootPanel.addComponent(new EmptySpace(new TerminalSize(0, 1)));

            // 4e) Two horizontal buttons at the bottom
            Panel bottomButtons = new Panel();
            bottomButtons.setLayoutManager(new GridLayout(2));
            bottomButtons.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));

            Button btnA = new Button("Button A", () -> {
                System.out.println("Button A clicked!");
                gui.getActiveWindow().close();
            });
            Button btnB = new Button("Button B", () -> {
                System.out.println("Button B clicked!");
                gui.getActiveWindow().close();
            });

            bottomButtons.addComponent(btnA);
            bottomButtons.addComponent(btnB);

            rootPanel.addComponent(bottomButtons);

            // 5) Set the panel as the window’s content and launch it
            window.setComponent(rootPanel);
            gui.addWindowAndWait(window);

            // Cleanup when window is closed
            screen.stopScreen();
            terminal.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean hasCrashed() {
        return hasCrashed;
    }
}
