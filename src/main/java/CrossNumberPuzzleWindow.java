import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.concurrent.atomic.AtomicReference;

/**
 * This window displays the cross number puzzle grid and questions. It allows
 * the user to input in numbers and then check if they are correct.
 */
public class CrossNumberPuzzleWindow extends HBox {

    private final boolean TESTING = false;
    private final CrossNumberPuzzleGame game;
    private GridPane puzzleGrid;
    private TextField[][] textFields;
    private StyleHelper textFieldsHelper;
    private Label outcomeLbl;
    private Label timeTakenLbl;
    private Label mistakesMadeLbl;
    private Label scoreLbl;
    private StyleHelper outcomeHelper;

    /**
     * Creates a cross number puzzle window and all its contents.
     */
    public CrossNumberPuzzleWindow(RealMain.Difficulty difficulty) {
        this.setPadding(new Insets(10));

        // Create the game.
        game = new CrossNumberPuzzleGame(this, difficulty);

        // Create the puzzle grid.
        createPuzzleGrid();

        // Create a separator between the grid and the questions and buttons.
        Separator separator = new Separator();
        separator.setOrientation(Orientation.VERTICAL);
        this.getChildren().add(separator);
        separator.setPadding(new Insets(0, 5, 0, 5));

        // Create the pane for questions and buttons.
        createQuestionsAndButtonsPane();
    }

    /**
     * Creates the puzzle grid and all its components.
     */
    private void createPuzzleGrid() {
        // Create the puzzle grid and set its properties.
        puzzleGrid = new GridPane();
        puzzleGrid.setMinWidth(RealMain.minWindowWidth / 2 - 20);
        puzzleGrid.setMinHeight(RealMain.minWindowHeight - 20);
        puzzleGrid.setPadding(new Insets(5));
        puzzleGrid.setAlignment(Pos.CENTER);
        setHgrow(puzzleGrid, Priority.ALWAYS);
        this.getChildren().add(puzzleGrid);

        // Get the column and row sizes.
        int rowSize = game.getRowSize();
        int colSize = game.getColSize();

        // Find the minimum dimension the square can be. This is for the minimum
        // window size and does not change.
        final double minDimension = Math.min((puzzleGrid.getMinWidth() / rowSize), (puzzleGrid.getMinHeight() / colSize));

        // Find the variable dimension of the square. This dimension is used
        // when the window is resized.
        NumberBinding varDimensionBinding = Bindings.min(puzzleGrid.widthProperty().divide(rowSize), puzzleGrid.heightProperty().divide(colSize));

        // Set the row constraints.
        RowConstraints rc = new RowConstraints(minDimension, minDimension, Double.MAX_VALUE);
        rc.maxHeightProperty().bind(varDimensionBinding);   // bind the maximum height property to the variable dimension binding
        rc.setVgrow(Priority.ALWAYS);   // ensure the row takes up any extra space
        // Add the row constraint to all the rows.
        for (int row = 0; row < colSize; row++) {
            puzzleGrid.getRowConstraints().add(rc);
        }

        // Set the column constraints.
        ColumnConstraints cc = new ColumnConstraints(minDimension, minDimension, Double.MAX_VALUE);
        cc.maxWidthProperty().bind(varDimensionBinding);    // bind the maximum width property to the variable dimension binding.
        cc.setHgrow(Priority.ALWAYS);   // ensure the column takes up any extra space.
        // Add the column constraint to all the columns.
        for (int col = 0; col < rowSize; col++) {
            puzzleGrid.getColumnConstraints().add(cc);
        }

        // Create puzzle grid contents.
        textFields = new TextField[colSize][rowSize];   // array for the text fields.
        textFieldsHelper = new StyleHelper();   // helper for setting styles
        int counter = 1;    // counter for question number

        // Iterate through the puzzle grid.
        for (int row = 0; row < colSize; row++) {
            for (int col = 0; col < rowSize; col++) {
                // Create the square.
                StackPane square = new StackPane();
                square.setPadding(new Insets(3));
                puzzleGrid.add(square, col, row);

                // Squares which are not part of the puzzle.
                if (game.getCharAt(row, col) == '-') {
                    square.getStyleClass().add("puzzle-square-unused");
                }
                // Squares which are part of the puzzle.
                else {
                    square.getStyleClass().add("puzzle-square-used");
                    // Create the text field.
                    TextField textField = new TextField();
                    textField.getStyleClass().add("puzzle-square-text-field");
                    textField.setAlignment(Pos.CENTER);
                    textFields[row][col] = textField;
                    StackPane.setAlignment(textField, Pos.CENTER);
                    square.getChildren().add(textField);

                    // Bind the dimensions of the text field to half o the dimensions of the square.
                    textField.maxHeightProperty().bind(varDimensionBinding.divide(2.0));
                    textField.maxWidthProperty().bind(varDimensionBinding.divide(2.0));

                    // Scale the text size with the square size.
                    square.heightProperty().addListener((observable, oldValue, newValue) -> {
                        textFieldsHelper.replaceStyles(textField.getStyle());
                        textFieldsHelper.addStyle("-fx-font-size: " + (newValue.intValue() / 4));
                        textField.setStyle(textFieldsHelper.getStyle());
                        textField.applyCss();
                    });

                    // Set constraints on the contents of the text field.
                    textField.textProperty().addListener((observable, oldValue, newValue) -> {
                        // Reset the text field back to black as the value may have changed.
                        textFieldsHelper.replaceStyles(textField.getStyle());
                        textFieldsHelper.addStyle("-fx-text-fill: black");
                        textField.setStyle(textFieldsHelper.getStyle());
                        textField.applyCss();

                        // Ensure the text field can only contain one character.
                        if (newValue.length() > 1) {
                            textField.setText(oldValue);
                        }

                        // Ensure the text field can only contain numbers.
                        if (!newValue.matches("[0-9]+")) {
                            if (!newValue.isEmpty()) {
                                textField.setText(oldValue);
                            }
                        }
                    });
                }

                // Squares which are the start of questions.
                if (game.getCharAt(row, col) == 'B' || game.getCharAt(row, col) == 'A' || game.getCharAt(row, col) == 'D') {
                    Label numLbl = new Label("" + counter);
                    numLbl.getStyleClass().add("puzzle-square-number-label");
                    StackPane.setAlignment(numLbl, Pos.TOP_LEFT);
                    square.getChildren().add(numLbl);
                    counter++;

                    // Scale the text size with the square size.
                    square.heightProperty().addListener((observable, oldValue, newValue) -> {
                        numLbl.setStyle("-fx-font-size: " + (newValue.intValue() / 6) + ";");
                        numLbl.applyCss();
                    });
                }
            }
        }

        // Testing info
        if (TESTING) {
            puzzleGrid.setStyle("-fx-border-color: pink;");

            ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> {
                System.out.println("Height: " + puzzleGrid.getHeight() + " Width: " + puzzleGrid.getWidth());
                System.out.println("Min height dimension: " + (puzzleGrid.getHeight() / colSize));
                System.out.println("Min width dimension: " + (puzzleGrid.getWidth() / rowSize));
                System.out.println("Min dimension: " + minDimension);
                System.out.println("Var dimension: " + varDimensionBinding.doubleValue());
                System.out.println("\n");
            };
            puzzleGrid.widthProperty().addListener(stageSizeListener);
            puzzleGrid.heightProperty().addListener(stageSizeListener);
        }
    }

    /**
     * Creates the pane for questions and buttons as well as all its contents.
     */
    private void createQuestionsAndButtonsPane() {

        // Create the pane to hold all the interface.
        BorderPane interfacePane = new BorderPane();
        interfacePane.setPadding(new Insets(5, 5, 5, 5));
        this.getChildren().add(interfacePane);

        // Create the scrollpane for the questions.
        ScrollPane questionScrollPane = new ScrollPane();
        questionScrollPane.getStyleClass().add("puzzle-scrollpane");
        questionScrollPane.setFitToWidth(true);
        interfacePane.setCenter(questionScrollPane);

        // Create the box for the questions.
        VBox questionBox = new VBox();
        questionScrollPane.setContent(questionBox);

        // Create the across heading label.
        Label acrossLabel = new Label("Going across: ");
        acrossLabel.getStyleClass().add("puzzle-heading-label");
        acrossLabel.setPadding(new Insets(0, 0, 5, 0));
        StackPane centredAcrossLabelPane = new StackPane(acrossLabel);
        centredAcrossLabelPane.setAlignment(Pos.CENTER);
        questionBox.getChildren().add(centredAcrossLabelPane);

        // Create the across question labels.
        for (CrossNumberPuzzleQuestion question : game.getQuestions()) {
            if (question.isGoingAcross()) {
                Label questionLabel;
                questionLabel = new Label(question.getQuestion(TESTING));
                questionLabel.getStyleClass().add("puzzle-question-label");
                questionLabel.setPadding(new Insets(5, 0, 0, 15));
                questionBox.getChildren().add(questionLabel);
            }
        }

        // Create the down heading label.
        Label downLabel = new Label("Going down: ");
        downLabel.getStyleClass().add("puzzle-heading-label");
        downLabel.setPadding(new Insets(5, 0, 5, 0));
        StackPane centeredDownLabelPane = new StackPane(downLabel);
        centeredDownLabelPane.setAlignment(Pos.CENTER);
        questionBox.getChildren().add(centeredDownLabelPane);

        // Create the down question labels.
        for (CrossNumberPuzzleQuestion question : game.getQuestions()) {
            if (!question.isGoingAcross()) {
                Label questionLabel;
                questionLabel = new Label(question.getQuestion(TESTING));
                questionLabel.getStyleClass().add("puzzle-question-label");
                questionLabel.setPadding(new Insets(5, 0, 0, 15));
                questionBox.getChildren().add(questionLabel);
            }
        }

        // Create the box to hold the outcome label, stats and buttons.
        VBox bottomBox = new VBox();
        bottomBox.setAlignment(Pos.CENTER);
        interfacePane.setBottom(bottomBox);

        // Create the outcome label.
        outcomeLbl = new Label();
        outcomeLbl.getStyleClass().add("outcome-label");
        outcomeHelper = new StyleHelper(outcomeLbl.getStyle());
        bottomBox.getChildren().add(outcomeLbl);

        // Create the time taken label.
        timeTakenLbl = new Label();
        timeTakenLbl.getStyleClass().add("stats-label");
        bottomBox.getChildren().add(timeTakenLbl);

        // Create the mistakes made label.
        mistakesMadeLbl = new Label();
        mistakesMadeLbl.getStyleClass().add("stats-label");
        bottomBox.getChildren().add(mistakesMadeLbl);

        // Create the score label.
        scoreLbl = new Label();
        scoreLbl.getStyleClass().add("stats-label");
        bottomBox.getChildren().add(scoreLbl);

        // Insert empty space
        Region spacer = new Region();
        spacer.setMinHeight(10);
        bottomBox.getChildren().add(spacer);

        // Create the pane for buttons.
        HBox buttonBox = new HBox();
        buttonBox.setSpacing(30);
        buttonBox.setAlignment(Pos.CENTER);
        bottomBox.getChildren().add(buttonBox);

        // Create the back button.
        Button backBtn = new Button("Back");
        backBtn.getStyleClass().add("puzzle-button");
        backBtn.setOnAction(this::back);
        buttonBox.getChildren().add(backBtn);

        // Create the restart button.
        Button restartBtn = new Button("Restart");
        restartBtn.getStyleClass().add("puzzle-button");
        restartBtn.setOnAction(this::restart);
        buttonBox.getChildren().add(restartBtn);

        // Create the submit button.
        Button submitBtn = new Button("Submit");
        submitBtn.getStyleClass().add("puzzle-button");
        submitBtn.setOnAction(this::submit);
        buttonBox.getChildren().add(submitBtn);

        // Border control
        if (TESTING) {
            interfacePane.setStyle("-fx-border-color: black;");
            questionBox.setStyle("-fx-border-color: green;");
            bottomBox.setStyle("-fx-border-color: brown;");
            buttonBox.setStyle("-fx-border-color: green;");
        }
    }

    /**
     * Moves back to the maths window.
     *
     * @param event The mouse event.
     */
    private void back(ActionEvent event) {
        this.getScene().setRoot(new MathsWindow());
    }

    /**
     * Restarts the game and the game window.
     *
     * @param event The mouse event.
     */
    private void restart(ActionEvent event) {
        this.getScene().setRoot(new CrossNumberPuzzleWindow(game.getDifficulty()));
    }

    /**
     * Checks the grid to see if the answers are correct. Highlights correct
     * answers green and incorrect answers red. Displays a message informing the
     * user if all their answers are correct or otherwise.
     *
     * @param event The mouse events
     */
    private void submit(ActionEvent event) {
        // Compare the contents of the text field array to the puzzle array from
        // the game, which contains the answers.
        boolean incorrectSquares = false;
        boolean emptySquares = false;
        for (int row = 0; row < game.getColSize(); row++) {
            for (int col = 0; col < game.getRowSize(); col++) {
                // Get the current text field.
                TextField currentField = textFields[row][col];

                // If the current field is null, then the square is not part of
                // the puzzle; we can skip comparing it.
                if (currentField == null) continue;

                // If the field is empty, set emptySquares to true.
                if (currentField.getText().isEmpty()) {
                    emptySquares = true;
                }
                // If the field has the correct character, change its colour to green.
                else if (currentField.getText().charAt(0) == game.getDigitAt(row, col))  {
                    textFieldsHelper.replaceStyles(currentField.getStyle());
                    textFieldsHelper.addStyle("-fx-text-fill: #00cc00");
                    currentField.setStyle(textFieldsHelper.getStyle());
                    currentField.applyCss();
                }
                // Otherwise the field has an incorrect character; change its
                // colour to red and set incorrectSquares to true.
                else {
                    incorrectSquares = true;
                    game.mistakeMade();
                    textFieldsHelper.replaceStyles(currentField.getStyle());
                    textFieldsHelper.addStyle("-fx-text-fill: orangered");
                    currentField.setStyle(textFieldsHelper.getStyle());
                    currentField.applyCss();
                }
            }
        }

        // Check the outcome.
        if (!incorrectSquares && !emptySquares) {
            // Signal the end of the game.
            game.end();

            // Play sound
            RealMain.playSound("success-sound.wav");

            // Set labels
            outcomeLbl.setText("Well done!");
            outcomeHelper.addStyle("-fx-text-fill: #00cc00");
            outcomeLbl.setStyle(outcomeHelper.getStyle());
            timeTakenLbl.setText("Time taken: " + game.getTimeElapsed() + " seconds");
            mistakesMadeLbl.setText("Mistakes made: " + game.getMistakesMade());
            scoreLbl.setText("Score: " + game.getScore());

            // Disable text fields.
            for (int row = 0; row < game.getColSize(); row++) {
                for (int col = 0; col < game.getRowSize(); col++) {
                    if (textFields[row][col] != null) textFields[row][col].setDisable(true);
                }
            }

            // Disable submit button.
            Button submitBtn = (Button) event.getSource();
            submitBtn.setDisable(true);

            // Check highscores
            game.checkHighscores();
        }
        else if (emptySquares) {
            outcomeLbl.setText("Puzzle incomplete!");
            outcomeHelper.addStyle("-fx-text-fill: -fx-text-color");
            outcomeLbl.setStyle(outcomeHelper.getStyle());
        }
        else //noinspection ConstantValue
            if (incorrectSquares) {
            outcomeLbl.setText("Try again!");
            outcomeHelper.addStyle("-fx-text-fill: orangered");
            outcomeLbl.setStyle(outcomeHelper.getStyle());
            RealMain.playSound("failure-sound.wav");
        }
    }

    /**
     * Creates a popup window, informing the user they have achieved a new
     * highscore and asking them for their name.
     *
     * @return The users name
     */
    public String getHighscoreName() {
        AtomicReference<String> userName = new AtomicReference<>("");

        Stage newStage = new Stage();
        newStage.setTitle("New Highscore!");
        newStage.initModality(Modality.APPLICATION_MODAL);  // ensures popup must be closed before continuing

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setMaxSize(RealMain.minWindowWidth - 50, RealMain.minWindowHeight - 50);

        VBox box = new VBox();
        box.setSpacing(10);
        box.setAlignment(Pos.CENTER);
        root.setCenter(box);

        Label titleLbl = new Label("Congratulations! New highscore!");
        titleLbl.getStyleClass().add("highscore-title-label");
        titleLbl.setWrapText(true);
        box.getChildren().add(titleLbl);

        Label textLbl = new Label("Please enter your name below (max 10 characters):");
        textLbl.getStyleClass().add("highscore-monospace-label");
        textLbl.setWrapText(true);
        box.getChildren().add(textLbl);

        TextField nameField = new TextField();
        nameField.getStyleClass().add("highscore-text-field");
        box.getChildren().add(nameField);
        // Ensure the text field can only contain up to 10 characters.
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 10) {
                nameField.setText(oldValue);
            }
        });

        Button submitBtn = new Button("Submit");
        submitBtn.getStyleClass().add("puzzle-button");
        submitBtn.setOnAction(value ->  {
            // Fill remaining space with spaces.
            StringBuilder s = new StringBuilder(nameField.getText());
            for (int i = 0; i < 10 - nameField.getLength(); i++) s.append(" ");
            userName.set(s.toString());
            newStage.close();
        });
        box.getChildren().add(submitBtn);

        Scene stageScene = new Scene(root);
        stageScene.getStylesheets().add("stylesheet.css");
        newStage.setScene(stageScene);
        newStage.showAndWait();

        return userName.get();
    }

}
