import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * This is the actual main class of the application. It starts the application
 * off by showing the menu window.
 */
public class RealMain extends Application {

    public static final String PUZZLE_FOLDER = "PuzzleTemplates";
    public static final String SOUNDS_FOLDER = "Sounds";
    public static final double minWindowWidth;
    public static final double minWindowHeight;
    public static final double minWindowDimension;
    public static final double initialTitleLabelFontSize;
    public static final double initialMenuButtonFontSize;
    public static final double initialMenuButtonWidth;
    public static final double initialMenuButtonHeight;

    // Calculate dimensions.
    static {
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        minWindowWidth = screenBounds.getWidth() * 0.5;
        minWindowHeight = screenBounds.getHeight() * 0.66;
        minWindowDimension = Math.min(minWindowWidth, minWindowHeight);

        initialTitleLabelFontSize = minWindowDimension * 0.15;
        initialMenuButtonFontSize = minWindowDimension * 0.055;
        initialMenuButtonWidth = minWindowDimension * 0.33;
        initialMenuButtonHeight = minWindowDimension * 0.13;
    }

    @Override
    public void start(Stage primaryStage) {
        // If running from inside a jar file, files must be copied first.
        if (insideJar()) {
            // Copy changelog.
            copyFile(getClass().getResourceAsStream("changelog.txt"), getBasePathForClass(RealMain.class) + File.separator + "changelog.txt");

            // Create the puzzle templates subfolders
            //noinspection ResultOfMethodCallIgnored
            new File(getBasePathForClass(RealMain.class) + File.separator + PUZZLE_FOLDER + File.separator + "easy").mkdirs();
            //noinspection ResultOfMethodCallIgnored
            new File(getBasePathForClass(RealMain.class) + File.separator + PUZZLE_FOLDER + File.separator + "hard").mkdirs();

            // Copy the puzzles.
            try (JarFile jarFile = new JarFile(getBasePathForClass(RealMain.class) + File.separator + getJarName())) {
                Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
                while (jarEntryEnumeration.hasMoreElements()) {
                    JarEntry jarEntry = jarEntryEnumeration.nextElement();
                    if (jarEntry.getName().matches(".*puzzle.*")) {
                        copyFile(getClass().getResourceAsStream(jarEntry.getName()), getBasePathForClass(RealMain.class) + File.separator + jarEntry.getName());
                    }
                }
            } catch (IOException e) {
                //noinspection CallToPrintStackTrace
                e.printStackTrace();
            }

            // Create the sounds folder
            //noinspection ResultOfMethodCallIgnored
            new File(getBasePathForClass(RealMain.class) + File.separator + SOUNDS_FOLDER).mkdirs();

            // Copy the sounds.
            try (JarFile jarFile = new JarFile(getBasePathForClass(RealMain.class) + File.separator + getJarName())) {
                Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
                while (jarEntryEnumeration.hasMoreElements()) {
                    JarEntry jarEntry = jarEntryEnumeration.nextElement();
                    if (jarEntry.getName().matches(".*sound\\.wav")) {
                        copyFile(getClass().getResourceAsStream(jarEntry.getName()), getBasePathForClass(RealMain.class) + File.separator + jarEntry.getName());
                    }
                }
            } catch (IOException e) {
                //noinspection CallToPrintStackTrace
                e.printStackTrace();
            }

            // Copy the app image.
            copyFile(getClass().getResourceAsStream("a-plus-tec.jpg"), getBasePathForClass(RealMain.class) + File.separator + "a-plus-tec.jpg");
        }

        // Create the window.
        MenuWindow menuWindow = new MenuWindow();
        Scene scene = new Scene(menuWindow, minWindowWidth, minWindowHeight);
        scene.getStylesheets().add("stylesheet.css");
        primaryStage.setTitle("A+ Tec");
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();
        primaryStage.setMinWidth(primaryStage.getWidth());
        primaryStage.setMinHeight(primaryStage.getHeight());
        InputStream stream = getClass().getResourceAsStream("a-plus-tec.jpg");
        if (stream != null) {
            Image icon = new Image(stream);
            primaryStage.getIcons().add(icon);
        }
    }

    /**
     * Copy a file from source to destination.
     *
     * @param source      The source.
     * @param destination The destination.
     */
    public static void copyFile(InputStream source, String destination) {
        try {
            Files.copy(source, Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            //noinspection CallToPrintStackTrace
            ex.printStackTrace();
        }
    }

    /**
     * Returns the absolute path of the current directory in which the given class file is.
     *
     * @param classFile The class file to search for.
     * @return The absolute path of the current directory in which the class file is.
     */
    public static String getBasePathForClass(Class<?> classFile) {
        String basePath = "";

        try {
            File file = new File(classFile.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            if (file.isFile() || file.getPath().endsWith(".jar") || file.getPath().endsWith(".zip")) {
                basePath = file.getParent();
            } else {
                basePath = file.getPath();
            }
        } catch (URISyntaxException ex1) {
            //noinspection CallToPrintStackTrace
            ex1.printStackTrace();
            try {
                URL url = classFile.getClassLoader().getResource("");
                if (url != null) {
                    File file = new File(url.toURI().getPath());
                    basePath = file.getAbsolutePath();
                }
            } catch (URISyntaxException ex2) {
                //noinspection CallToPrintStackTrace
                ex2.printStackTrace();
            }
        }

        return basePath;
    }

    /**
     * Checks if the program is running from inside a .jar file
     *
     * @return True if the program is running from inside a .jar file, false otherwise.
     */
    public static boolean insideJar() {
        boolean insideJar = false;

        try {
            File file = new File(RealMain.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());

            if (file.isFile() || file.getPath().endsWith(".jar") || file.getPath().endsWith(".zip")) {
                insideJar = true;
            }
        } catch (URISyntaxException ex) {
            //noinspection CallToPrintStackTrace
            ex.printStackTrace();
        }

        return insideJar;
    }

    /**
     * Gets the name of the jar file from which the program is running from.
     *
     * @return The name of the jar file.
     */
    public static String getJarName() {
        String jarName = null;

        try {
            File file = new File(RealMain.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            if (file.isFile() || file.getPath().endsWith(".jar") || file.getPath().endsWith(".zip")) {
                jarName = file.getName();
            }
        } catch (URISyntaxException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }

        return jarName;
    }

    /**
     * Plays a .wav sound clip.
     *
     * @param name The name of the clip, including the .wav extension.
     */
    public static void playSound(String name) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(getBasePathForClass(RealMain.class) + File.separator + SOUNDS_FOLDER + File.separator + name).getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception ex) {
            //noinspection CallToPrintStackTrace
            ex.printStackTrace();
        }
    }

    // Universal enum for difficulty.
    public enum Difficulty {
        EASY,
        MEDIUM,
        HARD
    }

    /*
        This enum holds all the different game types. Each game type has an
        associated name, game class and window class.
     */
    public enum GameType {
        CROSS_NUMBER_PUZZLE("Cross Number Puzzle", CrossNumberPuzzleGame.class, CrossNumberPuzzleWindow.class);

        public final String gameName;
        public final Class<?> gameClass;
        public final Class<?> windowClass;

        GameType(String gameName, Class<?> gameClass, Class<?> windowClass) {
            this.gameName = gameName;
            this.gameClass = gameClass;
            this.windowClass = windowClass;
        }

        public String getGameName() {
            return gameName;
        }

        public Class<?> getGameClass() {
            return gameClass;
        }

        public Class<?> getWindowClass() {
            return windowClass;
        }
    }

}
