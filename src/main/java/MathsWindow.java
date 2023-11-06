
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;

/**
 * This windows displays the options for maths games.
 */
public class MathsWindow extends BorderPane {

    private final Label title;
    private final ArrayList<Button> menuButtons = new ArrayList<>();

    /**
     * Creates the window and all its content.
     */
    public MathsWindow() {
        // Create the title.
        title = new Label("Maths Games!");
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

        // Create the button for the Cross Number Puzzle game.
        Button crossNumberPuzzleBtn = new Button("Cross Number Puzzle");
        crossNumberPuzzleBtn.getStyleClass().add("menu-button");
        crossNumberPuzzleBtn.setMinWidth(RealMain.initialMenuButtonWidth);
        crossNumberPuzzleBtn.setMinHeight(RealMain.initialMenuButtonHeight);
        crossNumberPuzzleBtn.setMaxWidth(RealMain.initialMenuButtonWidth);
        crossNumberPuzzleBtn.setFont(new Font(RealMain.initialMenuButtonFontSize));
        crossNumberPuzzleBtn.setTextAlignment(TextAlignment.CENTER);
        crossNumberPuzzleBtn.setWrapText(true);
        crossNumberPuzzleBtn.setOnAction(this::playCrossNumberPuzzle);
        buttonBox.getChildren().add(crossNumberPuzzleBtn);
        menuButtons.add(crossNumberPuzzleBtn);

        // Create the back button.
        Button backBtn = new Button("Back");
        backBtn.getStyleClass().add("menu-button");
        backBtn.setMinWidth(RealMain.initialMenuButtonWidth);
        backBtn.setMinHeight(RealMain.initialMenuButtonHeight);
        backBtn.setFont(new Font(RealMain.initialMenuButtonFontSize));
        backBtn.setOnAction(this::back);
        buttonBox.getChildren().add(backBtn);
        menuButtons.add(backBtn);

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
        };
        this.heightProperty().addListener(sizeListener);
        this.widthProperty().addListener(sizeListener);
    }

    /**
     * Changes the window to the difficulty window, with the Cross Number Puzzle
     * game window as the next window.
     *
     * @param actionEvent The mouse event
     */
    private void playCrossNumberPuzzle(ActionEvent actionEvent) {
        this.getScene().setRoot(new DifficultyWindow(RealMain.GameType.CROSS_NUMBER_PUZZLE, this));
    }

    /**
     * Changes the window to the previous window in the hierarchy.
     *
     * @param actionEvent The mouse event
     */
    private void back(ActionEvent actionEvent) {
        this.getScene().setRoot(new MenuWindow());
    }

}
