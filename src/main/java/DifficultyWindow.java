
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * This window allows you to choose the difficulty setting for any game.
 */
public class DifficultyWindow extends BorderPane {

    private final RealMain.GameType gameType;
    private final Label title;
    private final ArrayList<Button> menuButtons = new ArrayList<>();
    private final Button viewHighscoresBtn;
    private static final double initialHighscoresButtonFontSize = RealMain.minWindowDimension * 0.03;

    /**
     * Creates the window and all its buttons and labels.
     *
     * @param gameType The type of game to be played.
     * @param previousPane The pane from which this window was created.
     */
    public DifficultyWindow(RealMain.GameType gameType, Pane previousPane) {
        // The game type
        this.gameType = gameType;

        // Create the title.
        title = new Label(gameType.getGameName());
        title.getStyleClass().add("title-label");
        title.setFont(new Font(RealMain.initialTitleLabelFontSize));
        title.setPrefWidth(Double.MAX_VALUE);
        title.setAlignment(Pos.CENTER);
        title.setTextAlignment(TextAlignment.CENTER);
        title.setWrapText(true);
        this.setTop(title);

        // Create the box for buttons.
        VBox buttonBox = new VBox();
        buttonBox.setSpacing(20);
        buttonBox.setAlignment(Pos.CENTER);
        this.setCenter(buttonBox);

        // Create the easy button.
        Button easyBtn = new Button("Easy");
        easyBtn.getStyleClass().add("menu-button");
        easyBtn.setMinWidth(RealMain.initialMenuButtonWidth);
        easyBtn.setMinHeight(RealMain.initialMenuButtonHeight);
        easyBtn.setMaxWidth(RealMain.initialMenuButtonWidth);
        easyBtn.setFont(new Font(RealMain.initialMenuButtonFontSize));
        easyBtn.setWrapText(true);
        easyBtn.setOnAction(actionEvent ->  {
            try {
                // Get the game window class
                Class<?> windowClass = gameType.getWindowClass();
                // Get the constructor of the game window class.
                Constructor<?> cons = windowClass.getConstructor(RealMain.Difficulty.class);
                // Create the game object.
                Object o = cons.newInstance(RealMain.Difficulty.EASY);
                // Change the window.
                this.getScene().setRoot((Pane) o);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                //noinspection CallToPrintStackTrace
                e.printStackTrace();
            }
        });
        buttonBox.getChildren().add(easyBtn);
        menuButtons.add(easyBtn);

        Button hardBtn = new Button("Hard");
        hardBtn.getStyleClass().add("menu-button");
        hardBtn.setMinWidth(RealMain.initialMenuButtonWidth);
        hardBtn.setMinHeight(RealMain.initialMenuButtonHeight);
        hardBtn.setMaxWidth(RealMain.initialMenuButtonWidth);
        hardBtn.setFont(new Font(RealMain.initialMenuButtonFontSize));
        hardBtn.setWrapText(true);
        hardBtn.setOnAction(actionEvent ->  {
            try {
                // Get the game window class
                Class<?> windowClass = gameType.getWindowClass();
                // Get the constructor of the game window class.
                Constructor<?> cons = windowClass.getConstructor(RealMain.Difficulty.class);
                // Create the game object.
                Object o = cons.newInstance(RealMain.Difficulty.HARD);
                // Change the window.
                this.getScene().setRoot((Pane) o);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                //noinspection CallToPrintStackTrace
                e.printStackTrace();
            }
        });
        buttonBox.getChildren().add(hardBtn);
        menuButtons.add(hardBtn);

        // Create the back button.
        Button backBtn = new Button("Back");
        backBtn.getStyleClass().add("menu-button");
        backBtn.setMinWidth(RealMain.initialMenuButtonWidth);
        backBtn.setMinHeight(RealMain.initialMenuButtonHeight);
        backBtn.setMaxWidth(RealMain.initialMenuButtonWidth);
        backBtn.setFont(new Font(RealMain.initialMenuButtonFontSize));
        backBtn.setWrapText(true);
        backBtn.setOnAction(actionEvent -> this.getScene().setRoot(previousPane));
        buttonBox.getChildren().add(backBtn);
        menuButtons.add(backBtn);

        // Create the box at the bottom of the window.
        BorderPane bottomBox = new BorderPane();
        bottomBox.setPadding(new Insets(5));
        this.setBottom(bottomBox);

        // Create the button to view highscores.
        viewHighscoresBtn = new Button("View Highscores");
        viewHighscoresBtn.getStyleClass().add("highscore-button");
        viewHighscoresBtn.setFont(new Font(initialHighscoresButtonFontSize));
        viewHighscoresBtn.setPadding(new Insets(2, 4, 2, 4));
        viewHighscoresBtn.setOnAction(this::viewHighscores);
        bottomBox.setRight(viewHighscoresBtn);

        // Add the listener to scale the GUI according to window size.
        ChangeListener<Number> sizeListener = (observable, oldValue, newValue) -> {
            // Calculate the multiplier
            double heightMultiplier = this.getHeight() / RealMain.minWindowHeight;
            double widthMultiplier = this.getWidth() / RealMain.minWindowWidth;
            double multiplier = Math.min(heightMultiplier, widthMultiplier);

            // Update the title label.
            title.setFont(new Font(RealMain.initialTitleLabelFontSize * multiplier));

            // Update the menu buttons.
            for (Button btn : menuButtons) {
                btn.setFont(new Font(RealMain.initialMenuButtonFontSize * multiplier));
                btn.setMinWidth(RealMain.initialMenuButtonWidth * multiplier);
                btn.setMinHeight(RealMain.initialMenuButtonHeight * multiplier);
                btn.setMaxWidth(RealMain.initialMenuButtonWidth);
            }

            // Update the highscore button.
            viewHighscoresBtn.setFont((new Font(initialHighscoresButtonFontSize * multiplier)));
        };
        this.heightProperty().addListener(sizeListener);
        this.widthProperty().addListener(sizeListener);
    }

    /**
     * This method is called as a result of pressing the View Highscores button.
     * It opens up a popup window with the highscores for the selected game.
     *
     * @param actionEvent The action event that caused this method to be called.
     */
    private void viewHighscores(ActionEvent actionEvent) {
        // Get the highscores.
        ArrayList<Highscore> highscores = null;
        try {
            Class<?> gameClass = gameType.getGameClass();
            Method method = gameClass.getMethod("getHighscores");
            //noinspection unchecked
            highscores = (ArrayList<Highscore>) method.invoke(null);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            //noinspection CallToPrintStackTrace
            exception.printStackTrace();
        }
        assert highscores != null;

        // Create the window
        Stage newStage = new Stage();
        newStage.setTitle("Highscores");
        newStage.initModality(Modality.APPLICATION_MODAL);  // ensures popup must be closed before continuing
        newStage.setResizable(false);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setMaxSize(RealMain.minWindowWidth - 50, RealMain.minWindowHeight - 50);

        VBox box = new VBox();
        box.setSpacing(10);
        box.setAlignment(Pos.CENTER_LEFT);
        root.setCenter(box);

        Label titleLbl = new Label(gameType.getGameName() + " highscores!");
        titleLbl.getStyleClass().add("highscore-title-label");
        titleLbl.setWrapText(true);
        box.getChildren().add(titleLbl);

        for (RealMain.Difficulty difficulty : RealMain.Difficulty.values()) {
            Label diffLbl = new Label(difficulty.toString());
            diffLbl.getStyleClass().add("highscore-monospace-label");
            diffLbl.setWrapText(true);
            box.getChildren().add(diffLbl);

            boolean noHighscores = true;
            for (Highscore hs : highscores) {
                if (hs.getDifficulty() == difficulty) {
                    noHighscores = false;
                    Label hsLbl = new Label(hs.toString());
                    hsLbl.getStyleClass().add("highscore-monospace-label");
                    hsLbl.setWrapText(true);
                    box.getChildren().add(hsLbl);
                }
            }
            if (noHighscores) box.getChildren().remove(diffLbl);
        }

        Scene stageScene = new Scene(root);
        stageScene.getStylesheets().add("stylesheet.css");
        newStage.setScene(stageScene);
        newStage.showAndWait();
    }

}
