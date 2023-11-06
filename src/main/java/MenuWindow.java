import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class is the menu of the application. Here, the user chooses the subject
 * they wish to play.
 */

public class MenuWindow extends BorderPane {

    private final Label title;
    private final ArrayList<Button> menuButtons = new ArrayList<>();
    private final Label versionLbl;
    private static final double initialVersionLabelFontSize = RealMain.minWindowDimension * 0.035;
    private final Button viewChangelogBtn;
    private static final double initialChangelogButtonFontSize = RealMain.minWindowDimension * 0.03;

    // Changelog stuff
    private Label changelogEntryLbl;     // the label showing a changelog entry.
    private Button previousVersionBtn;      // button to view the changelog entry for the previous version.
    private Button nextVersionBtn;      // button to view the changelog entry for the next version.
    private int howFarBack;     // a counter keeping track of which changelog entry is being shown, with 0 being the latest entry.

    /**
     * Creates the window and all its buttons and labels.
     */
    public MenuWindow() {
        // Create the title.
        title = new Label("A+ Tec Games!");
        title.getStyleClass().add("title-label");
        title.setFont(new Font(RealMain.initialTitleLabelFontSize));
        title.setPrefWidth(Double.MAX_VALUE);
        title.setAlignment(Pos.CENTER);
        title.setTextAlignment(TextAlignment.CENTER);
        title.setWrapText(true);
        this.setTop(title);

        // Create the box for buttons.
        VBox buttonBox = new VBox();
        buttonBox.setSpacing(50);
        buttonBox.setAlignment(Pos.CENTER);
        this.setCenter(buttonBox);

        // Create the grid for buttons.
        GridPane buttonPane = new GridPane();
        buttonPane.setHgap(50);
        buttonPane.setVgap(50);
        buttonPane.setAlignment(Pos.CENTER);
        buttonBox.getChildren().add(buttonPane);

        // Create the maths button.
        Button mathsBtn = new Button("Maths");
        mathsBtn.getStyleClass().add("menu-button");
        mathsBtn.setMinWidth(RealMain.initialMenuButtonWidth);
        mathsBtn.setMinHeight(RealMain.initialMenuButtonHeight);
        mathsBtn.setMaxWidth(RealMain.initialMenuButtonWidth);
        mathsBtn.setFont(new Font(RealMain.initialMenuButtonFontSize));
        mathsBtn.setOnAction(this::playMaths);
        buttonPane.add(mathsBtn, 0, 0);
        menuButtons.add(mathsBtn);

        // Create the physics button
        Button physicsBtn = new Button("Physics");
        physicsBtn.getStyleClass().add("menu-button");
        physicsBtn.setMinWidth(RealMain.initialMenuButtonWidth);
        physicsBtn.setMinHeight(RealMain.initialMenuButtonHeight);
        physicsBtn.setMaxWidth(RealMain.initialMenuButtonWidth);
        physicsBtn.setFont(new Font(RealMain.initialMenuButtonFontSize));
        physicsBtn.setOnAction(this::playPhysics);
        physicsBtn.setDisable(true);
        buttonPane.add(physicsBtn, 1, 0);
        menuButtons.add(physicsBtn);

        // Create the biology button
        Button biologyBtn = new Button("Biology");
        biologyBtn.getStyleClass().add("menu-button");
        biologyBtn.setMinWidth(RealMain.initialMenuButtonWidth);
        biologyBtn.setMinHeight(RealMain.initialMenuButtonHeight);
        biologyBtn.setMaxWidth(RealMain.initialMenuButtonWidth);
        biologyBtn.setFont(new Font(RealMain.initialMenuButtonFontSize));
        biologyBtn.setOnAction(this::playBiology);
        biologyBtn.setDisable(true);
        buttonPane.add(biologyBtn, 0, 1);
        menuButtons.add(biologyBtn);

        // Create the chemistry button
        Button chemistryBtn = new Button("Chemistry");
        chemistryBtn.getStyleClass().add("menu-button");
        chemistryBtn.setMinWidth(RealMain.initialMenuButtonWidth);
        chemistryBtn.setMinHeight(RealMain.initialMenuButtonHeight);
        chemistryBtn.setMaxWidth(RealMain.initialMenuButtonWidth);
        chemistryBtn.setFont(new Font(RealMain.initialMenuButtonFontSize));
        chemistryBtn.setOnAction(this::playChemistry);
        chemistryBtn.setDisable(true);
        buttonPane.add(chemistryBtn, 1, 1);
        menuButtons.add(chemistryBtn);

        // Create the exit button
        Button exitBtn = new Button("Exit");
        exitBtn.getStyleClass().add("menu-button");
        exitBtn.setMinWidth(RealMain.initialMenuButtonWidth);
        exitBtn.setMinHeight(RealMain.initialMenuButtonHeight);
        exitBtn.setMaxWidth(RealMain.initialMenuButtonWidth);
        exitBtn.setFont(new Font(RealMain.initialMenuButtonFontSize));
        exitBtn.setOnAction(this::exit);
        buttonBox.getChildren().add(exitBtn);
        menuButtons.add(exitBtn);

        // Create the box at the bottom of the window.
        BorderPane bottomBox = new BorderPane();
        bottomBox.setPadding(new Insets(5));
        this.setBottom(bottomBox);

        // Create the version number label.
        versionLbl = new Label();
        versionLbl.getStyleClass().add("version-label");
        versionLbl.setFont(new Font(initialVersionLabelFontSize));
        BorderPane.setAlignment(versionLbl, Pos.BOTTOM_LEFT);
        versionLbl.setText(getVersion());
        bottomBox.setLeft(versionLbl);

        // Create the button to view the changelog.
        viewChangelogBtn = new Button("View Changelog");
        viewChangelogBtn.getStyleClass().add("changelog-button");
        viewChangelogBtn.setFont(new Font(initialChangelogButtonFontSize));
        viewChangelogBtn.setPadding(new Insets(2, 4, 2, 4));
        viewChangelogBtn.setOnAction(this::viewChangelog);
        bottomBox.setRight(viewChangelogBtn);

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

            // Update the changelog label and button.
            versionLbl.setFont(new Font(initialVersionLabelFontSize * multiplier));
            viewChangelogBtn.setFont(new Font(initialChangelogButtonFontSize * multiplier));
        };
        this.heightProperty().addListener(sizeListener);
        this.widthProperty().addListener(sizeListener);
    }

    /**
     * This method gets called when the maths button is pressed. It replaces the
     * current window with the maths window.
     *
     * @param actionEvent The button being pressed.
     */
    private void playMaths(ActionEvent actionEvent) {
        this.getScene().setRoot(new MathsWindow());
    }

    /**
     * This method gets called when the physics button is pressed. It replaces
     * the current window with the physics window.
     *
     * @param actionEvent The button being pressed.
     */
    private void playPhysics(ActionEvent actionEvent) {
    }

    /**
     * This method gets called when the biology button is pressed. It replaces
     * the current window with the biology window.
     *
     * @param actionEvent The button being pressed.
     */
    private void playBiology(ActionEvent actionEvent) {
    }

    /**
     * This method gets called when the chemistry button is pressed. It replaces
     * the current window with the chemistry window.
     *
     * @param actionEvent The button being pressed.
     */
    private void playChemistry(ActionEvent actionEvent) {
    }

    /**
     * This method gets called when the exit button is pressed. It exits the
     * application.
     *
     * @param actionEvent The button being pressed.
     */
    private void exit(ActionEvent actionEvent) {
        Platform.exit();
    }

    /**
     * Gets the latest version number and its release date from the changelog
     *
     * @return The latest version number and its release date.
     */
    private String getVersion() {
        String version = null;
        File changelog = new File(RealMain.getBasePathForClass(RealMain.class) + File.separator + "changelog.txt");
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(changelog));
            String line = reader.readLine();
            while (line != null) {
                if (!line.matches("\\*\\*\\s*Version.*")) {
                    line = reader.readLine();
                }
                else {
                    // Remove the leading "** ".
                    version = line.replaceAll("\\*\\*\\s*", "");
                    // Remove the date
                    version = version.substring(0, version.indexOf("-"));
                    break;
                }
            }
            reader.close();
        }
        catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
        // Close the reader
        finally {
            try {
                if (reader != null)  {
                    reader.close();
                }
            }
            catch (IOException ex) {
                //noinspection CallToPrintStackTrace
                ex.printStackTrace();
            }
        }

        return version;
    }

    /**
     * This method gets called when the view changelog button is pressed. It
     * opens a popup window with the most recent entry in the changelog.
     *
     * @param actionEvent The button being pressed.
     */
    private void viewChangelog(ActionEvent actionEvent) {
        howFarBack = 0;

        Stage changelogStage = new Stage();
        changelogStage.setTitle("Changelog");
        changelogStage.initModality(Modality.APPLICATION_MODAL);  // ensures popup must be closed before continuing

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setPrefSize(RealMain.minWindowWidth - 50,RealMain.minWindowHeight - 50);

        // Create a property to scale the scroll pane when the changelog entry changes.
        ObjectProperty<Integer> changelogEntryProperty = new SimpleObjectProperty<>();
        root.getProperties().put("changelogEntryProperty", changelogEntryProperty);

        changelogEntryLbl = new Label();
        changelogEntryLbl.getStyleClass().add("changelog-entry-label");
        changelogEntryLbl.setWrapText(true);
        changelogEntryLbl.setText(getChangelogEntry(howFarBack));

        ScrollPane changelogPane = new ScrollPane();
        changelogPane.getStyleClass().add("changelog-scrollpane");
        changelogPane.setFitToWidth(true);
        changelogPane.setContent(changelogEntryLbl);
        root.setTop(changelogPane);

        BorderPane buttonPane = new BorderPane();
        buttonPane.setPadding(new Insets(20, 5, 5, 5));
        root.setBottom(buttonPane);

        previousVersionBtn = new Button("Previous Version");
        previousVersionBtn.getStyleClass().add("change-version-button");
        previousVersionBtn.setMinWidth(RealMain.minWindowDimension * 0.25);
        previousVersionBtn.setOnAction(event -> previousChangelogEntry(event, changelogEntryProperty));
        buttonPane.setLeft(previousVersionBtn);

        nextVersionBtn = new Button("Next Version");
        nextVersionBtn.getStyleClass().add("change-version-button");
        nextVersionBtn.setMinWidth(RealMain.minWindowDimension * 0.25);
        nextVersionBtn.setOnAction(event -> nextChangelogEntry(event, changelogEntryProperty));
        nextVersionBtn.setDisable(true);
        buttonPane.setRight(nextVersionBtn);

        Scene stageScene = new Scene(root);
        stageScene.getStylesheets().add("stylesheet.css");
        changelogStage.setScene(stageScene);
        changelogStage.show();

        // Create the listener to scale the scroll pane with the window size.
        root.heightProperty().addListener(((observable, oldValue, newValue) -> changelogPane.setPrefViewportHeight(root.getHeight() - 100)));

        // Add a listener to scale the scroll pane when the changelog entry changes.
        changelogEntryProperty.addListener((observable, oldValue, newValue) -> changelogPane.setPrefViewportHeight(root.getHeight() - 100));
        changelogEntryProperty.set(howFarBack);
    }

    /**
     * Gets the specified changelog entry.
     *
     * @param howFarBack How far back to go for the entry. A value of 0 will get the most recent entry.
     * @return The most specified entry from the changelog.
     */
    private String getChangelogEntry(int howFarBack) {
        StringBuilder entry = new StringBuilder();
        File changelog = new File(RealMain.getBasePathForClass(RealMain.class) + File.separator + "changelog.txt");
        BufferedReader reader = null;
        int counter = 0;    // counter for keeping track of which entry is being viewed, with 0 being the most recent entry.

        try {
            reader = new BufferedReader(new FileReader(changelog));
            String line = reader.readLine();
            while (line != null) {
                if (!line.matches("\\*\\*\\s*Version.*")) {
                    line = reader.readLine();
                }
                else {
                    // Add the version number line to the entry and move onto the next line.
                    entry.append(line).append("\n");
                    line = reader.readLine();
                    // Keep adding lines until you get to another version number line or end of file.
                    while (line != null && !line.matches("\\*\\*\\s*Version.*")) {
                        // If the line starts with a word character, it is not the start of a new line but a
                        // continuation of the previous line.
                        if (line.matches("\\w.*")) {
                            entry.append(line);
                        }
                        // Otherwise it is the start of a new line.
                        else {
                            entry.append("\n").append(line);
                        }
                        line = reader.readLine();
                    }
                    // If this was the specified entry, break and return. Else reset the string and go again.
                    if (counter == howFarBack) break;
                    else {
                        entry = new StringBuilder();
                        counter++;
                    }
                }
            }
            reader.close();
        }
        catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
        // Close the reader
        finally {
            try {
                if (reader != null)  {
                    reader.close();
                }
            }
            catch (IOException ex) {
                //noinspection CallToPrintStackTrace
                ex.printStackTrace();
            }
        }

        return entry.toString().trim();
    }

    /**
     * Moves to the previous changelog version.
     */
    private void previousChangelogEntry(ActionEvent ignoredActionEvent, ObjectProperty<Integer> changelogEntryProperty){
        howFarBack++;

        // Since we are about to view a previous entry, there must be a next
        // entry (the one being displayed now), so we can enable the next
        // version button.
        nextVersionBtn.setDisable(false);
        // If the next entry is blank we have reached the last entry; we need
        // to disable the previous entry button.
        if (getChangelogEntry(howFarBack + 1).isEmpty()) {
            previousVersionBtn.setDisable(true);
        }

        changelogEntryLbl.setText(getChangelogEntry(howFarBack));
        changelogEntryProperty.set(howFarBack);
    }

    /**
     * Moves to the next changelog version.
     */
    private void nextChangelogEntry(ActionEvent ignoredActionEvent, ObjectProperty<Integer> changelogEntryProperty){
        howFarBack--;

        // Since we are about to view a next entry, there must be a previous
        // entry (the one being displayed now), so we can enable the previous
        // version button.
        previousVersionBtn.setDisable(false);
        // If the version is now the most recent one, disable the next version
        // button.
        if (howFarBack == 0) {
            nextVersionBtn.setDisable(true);
        }

        changelogEntryLbl.setText(getChangelogEntry(howFarBack));
        changelogEntryProperty.set(howFarBack);
    }

}
