import java.io.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * This class handles the mechanics of the Cross Number Puzzle game.
 */
public class CrossNumberPuzzleGame {

    private final boolean TESTING = false;
    private final Random rand = new Random();
    private final CrossNumberPuzzleWindow gameWindow;
    private final RealMain.Difficulty difficulty;
    private char[][] puzzleTemplate;
    private char[][] puzzleArray;
    private int colSize;
    private int rowSize;
    private final ArrayList<CrossNumberPuzzleQuestion> questions = new ArrayList<>();
    private final long startTime = System.currentTimeMillis();
    private long timeElapsed;   // in seconds
    private int mistakesMade = 0;
    private int score;
    private static ArrayList<Highscore> highscores;

    /*
        This code block attempts to load the highscore file. If it is
        successful, it will load the highscores into the arraylist. If it is
        unsuccessful, it will create a new highscore file and load an empty
        arraylist.
     */
    static {
        // Try reading the highscore file.
        try {
            FileInputStream fin = new FileInputStream(RealMain.getBasePathForClass(RealMain.class) + File.separator + "CNP-highscores.dat");
            ObjectInputStream ois = new ObjectInputStream(fin);
            //noinspection unchecked
            highscores = (ArrayList<Highscore>) ois.readObject();
            ois.close();
        }
        // If it does not exist, create a new highscore file.
        catch (FileNotFoundException fNFE) {
            highscores = new ArrayList<>();
            try {
                FileOutputStream fileOutStream = new FileOutputStream(RealMain.getBasePathForClass(RealMain.class) + File.separator + "CNP-highscores.dat");
                ObjectOutputStream objectOutStream = new ObjectOutputStream(fileOutStream);
                objectOutStream.writeObject(highscores);
                objectOutStream.close();
            }
            catch (Exception e) {
                //noinspection CallToPrintStackTrace
                e.printStackTrace();
            }
        }
        catch (Exception e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    /**
     * Creates a new cross number puzzle.
     */
    public CrossNumberPuzzleGame(CrossNumberPuzzleWindow gameWindow, RealMain.Difficulty difficulty) {
        this.difficulty = difficulty;
        this.gameWindow = gameWindow;

        createPuzzleTemplate();
        createPuzzleArray();
        fillPuzzleArray();
        createQuestions();

        if (TESTING) {
            System.out.println("Arrays:");
            System.out.println();
            printArray(puzzleTemplate);
            printArray(puzzleArray);

            System.out.println("Questions:");
            for (CrossNumberPuzzleQuestion question : questions) {
                System.out.println(question);
            }
        }
    }

    /**
     * Prints out a 2D char array to the console.
     *
     * @param arr The 2D char array to be printed.
     */
    private void printArray(char[][] arr) {
        for (int row = 0; row < colSize; row++) {
            for (int col = 0; col < rowSize; col++) {
                System.out.print(arr[row][col] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Picks a random file from the given directory and returns it.
     *
     * @param path The path to the directory.
     * @return A file from the directory, picked at random.
     */
    private File getRandomFile(String path) {
        // File to return.
        File randomFile;

        // Directory to look in.
        File dir = new File(RealMain.getBasePathForClass(RealMain.class) + File.separator + path);
        if (TESTING) System.out.println(dir.getAbsolutePath());

        // Array of files inside directory.
        File[] files = dir.listFiles();
        assert files != null;

        // Pick one of the files randomly.
        randomFile = files[new Random().nextInt(files.length)];

        // Return it.
        return randomFile;
    }

    /**
     * Randomly selects one of the puzzle template from the template folder and
     * creates a 2D array of it.
     */
    private void createPuzzleTemplate() {
        // Pick one of the puzzles at random.
        String diff = difficulty.name().toLowerCase();
        File puzzleFile = getRandomFile(RealMain.PUZZLE_FOLDER + File.separator + diff);
        BufferedReader reader = null;

        // Read the puzzle from file.
        try {
            // Get the column size i.e. the number of rows.
            reader = new BufferedReader(new FileReader(puzzleFile));
            colSize = 0;
            while (reader.readLine() != null) colSize++;
            reader.close();

            // Get the row size i.e. the number of columns.
            reader = new BufferedReader(new FileReader(puzzleFile));
            rowSize = reader.readLine().replaceAll("\\s", "").length();
            reader.close();

            // Create a 2D array representing the puzzle.
            int row = 0;    // row counter
            int col = 0;    // column counter
            puzzleTemplate = new char[colSize][rowSize];
            reader = new BufferedReader(new FileReader(puzzleFile));
            int currentCharInt;    // the character is initially an integer

            while ((currentCharInt = reader.read()) != -1) {
                char currentChar = (char) currentCharInt;  // cast the integer to a character

                // If the character is a space, skip it.
                if (Character.isWhitespace(currentChar)) continue;

                // Otherwise add the character to the array.
                puzzleTemplate[row][col] = currentChar;

                // Increase the column counter, check for overflow and
                // potentially reset the column counter and increase the row
                // counter.
                col++;
                if (col >= rowSize) {
                    col = 0;
                    row++;
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
    }

    /**
     * Copies the puzzle template into a new array. This new array will then be
     * filled out.
     */
    private void createPuzzleArray() {
        puzzleArray = new char[colSize][rowSize];
        for (int row = 0; row < colSize; row++) {
            System.arraycopy(puzzleTemplate[row], 0, puzzleArray[row], 0, rowSize);
        }
    }

    /**
     * Fills the puzzle array with random numbers.
     */
    private void fillPuzzleArray() {
        for (int row = 0; row < colSize; row++) {
            for (int col = 0; col < rowSize; col++) {
                char currentChar = puzzleArray[row][col];

                // If the char is 'B' or 'A', then this square is the start of a number going across.
                if (currentChar == 'B' || currentChar == 'A') {
                    fill(true, row, col);
                }

                // If the char is 'B' or 'D', then this square is the start of a number going downwards.
                if (currentChar == 'B' || currentChar == 'D') {
                    fill(false, row, col);
                }
            }
        }
    }

    /**
     * Fills the array with random numbers going across or down until it hits an
     * empty square, marked by the char '-'. If it comes across a special
     * character while doing so, it resolves it first via a recursive method
     * call. The very first digit is never a zero.
     *
     * @param across True if going across, false if going down.
     * @param row The current row.
     * @param col The current col.
     */
    private void fill(boolean across, int row, int col) {
        char currentChar = puzzleArray[row][col];
        boolean firstDigit = true;

        while (currentChar != '-') {
            // We don't want the start of a number to be a zero. So the very first digit of a number is a special case.
            if (firstDigit) {
                puzzleArray[row][col] = (char) (rand.nextInt(9) + 1 + '0');
                firstDigit = false;
            }
            // We only change the character if it is not already a number.
            else if (!Character.isDigit(currentChar)) {
                puzzleArray[row][col] = (char) (rand.nextInt(10) + '0');
            }

            // Increase row or col and check if out of bounds.
            if (across) {
                col++;
                if (col >= rowSize) break;
            }
            else {
                row++;
                if (row >= colSize) break;
            }

            // Move to the next character.
            currentChar = puzzleArray[row][col];

            // If the next character is a special character, sort it out first via a recursive method call.
            if (currentChar == 'A') {
                fill(true, row, col);
            }
            else if (currentChar == 'D') {
                fill (false, row, col);
            }

            // This is necessary as the character may have changed as a result of the if statement above.
            currentChar = puzzleArray[row][col];
        }
    }

    /**
     * Creates questions using the puzzle template and puzzle array.
     */
    private void createQuestions() {
        int counter = 1;
        for (int row = 0; row < colSize; row++) {
            for (int col = 0; col < rowSize; col++) {
                // templateChar is the character from the template array. It is
                // used to check for special characters.
                char templateChar = puzzleTemplate[row][col];

                // If the char is 'B' or 'A', then this square is the start of a
                // number going across.
                if (templateChar == 'B' || templateChar == 'A') {
                    // Get the answer.
                    int answer = Integer.parseInt(readAnswer(true, row, col));

                    // Pick an operator.
                    char operator = ' ';
                    while (operator == ' ') {
                        int randomNumber = rand.nextInt(4);
                        if (randomNumber == 0) operator = '+';
                        if (randomNumber == 1) operator = '-';
                        if (randomNumber == 2) operator = '/';
                        // If the number is prime, the operator cannot be multiplication.
                        if (randomNumber == 3 && !isPrime(answer)) operator = 'x';
                    }

                    // Create the operands.
                    int[] operands = createOperands(answer, operator);
                    int operand1 = operands[0];
                    int operand2 = operands[1];

                    // Create the question and add it to the list.
                    questions.add(new CrossNumberPuzzleQuestion(counter, true, answer, operator, operand1, operand2));
                }

                // If the char is 'B' or 'D', then this square is the start of a
                // number going downwards.
                if (templateChar == 'B' || templateChar == 'D') {
                    // Get the answer.
                    int answer = Integer.parseInt(readAnswer(false, row, col));

                    // Pick an operator.
                    char operator = ' ';
                    while (operator == ' ') {
                        int randomNumber = rand.nextInt(4);
                        if (randomNumber == 0) operator = '+';
                        if (randomNumber == 1) operator = '-';
                        if (randomNumber == 2) operator = '/';
                        // If the answer is prime, the operator cannot be multiplication.
                        if (randomNumber == 3 && !isPrime(answer)) operator = 'x';
                    }

                    // Create the operands.
                    int[] operands = createOperands(answer, operator);
                    int operand1 = operands[0];
                    int operand2 = operands[1];

                    // Create the question and add it to the list.
                    questions.add(new CrossNumberPuzzleQuestion(counter, false, answer, operator, operand1, operand2));
                }
                // Increment the counter if a question was created.
                if (templateChar == 'B' || templateChar == 'A' || templateChar == 'D') counter++;
            }
        }
    }

    /**
     * Reads the puzzle array for answers.
     *
     * @param across True if the answer is going across, false if going down.
     * @param row The row where the answer begins.
     * @param col THe column where the answer begins.
     * @return The answer.
     */
    private String readAnswer(boolean across, int row, int col) {
        // arrayChar is the character from the puzzle array, i.e. it is a digit.
        char arrayChar = puzzleArray[row][col];
        // Answer will be the whole number
        StringBuilder answer = new StringBuilder();

        while (arrayChar != '-') {
            // Add the digit to the answer.
            answer.append(arrayChar);

            // Increase either row or col and check if out of bounds.
            if (across) {
                col++;
                if (col >= rowSize) break;
            }
            else {
                row++;
                if (row >= colSize) break;
            }

            // Go to the next char.
            arrayChar = puzzleArray[row][col];
        }

        return answer.toString();
    }

    /**
     * Checks if a number is prime.
     *
     * @param number The number to be checked.
     * @return True if the number is prime, false otherwise.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isPrime(int number) {
        boolean prime = true;

        for(int i = 2; i <= number/2; ++i) {
            if (number % i == 0) {
                prime = false;
                break;
            }
        }

        return prime;
    }

    /**
     * Creates the two operands that, when used with the operator, result in the answer.
     *
     * @param answer The answer.
     * @param operator The operator.
     * @return Two operands that give the answer when the operator is applied.
     */
    private int[] createOperands(int answer, char operator) {
        int[] operands = new int[2];

        if (operator == '+') {
            operands[0] = rand.nextInt(answer - 1) + 1;
            operands[1] = answer - operands[0];
        }

        if (operator == '-') {
            operands[1] = rand.nextInt(1000) + 1;
            operands[0] = answer + operands[1];
        }

        if (operator == '/') {
            if (difficulty == RealMain.Difficulty.EASY) {
                operands[1] = rand.nextInt(8) + 2;
            }
            else if (difficulty == RealMain.Difficulty.HARD) {
                operands[1] = rand.nextInt(98) + 2;
            }
            else System.out.println("Something went wrong creating operands for division!");
            operands[0] = answer * operands[1];
        }

        if (operator == 'x') {
            ArrayList<Integer> factors = new ArrayList<>();
            for (int i = 2; i <= answer / 2; i++) {
                if (answer % i == 0) factors.add(i);
            }
            operands[0] = factors.get(rand.nextInt(factors.size()));
            operands[1] = answer / operands[0];
        }

        return operands;
    }

    /**
     * @return The list of questions.
     */
    public ArrayList<CrossNumberPuzzleQuestion> getQuestions() {
        return questions;
    }

    /**
     * @return The column size i.e. the number of rows
     */
    public int getColSize() {return colSize;}

    /**
     * @return The row size i.e. the number of columns
     */
    public int getRowSize() {return rowSize;}

    /**
     * Returns a character from the specified row and column from the puzzle
     * template array.
     *
     * @return The character at the specified row and column from the puzzle template array.
     * @param row The row the character is at.
     * @param col The col the character is at.
     */
    public char getCharAt(int row, int col) {return puzzleTemplate[row][col];}

    /**
     * Returns a character from the specified row and column from the puzzle array.
     *
     * @return The character at the specified row and column from the puzzle array.
     * @param row The row the character is at.
     * @param col The col the character is at.
     */
    public char getDigitAt(int row, int col) {return puzzleArray[row][col];}

    /**
     * Increases the mistakes made counter
     */
    public void mistakeMade() {
        mistakesMade++;
    }

    /**
     * @return The number of mistakes made during the game.
     */
    public int getMistakesMade() {
        return mistakesMade;
    }

    /**
     * Calling this method signals the end of the game. Stats are then calculated.
     */
    public void end() {
        timeElapsed = (System.currentTimeMillis() - startTime) / 1000;
        score = calculateScore();
    }

    /**
     * @return The time elapsed since the game was created, in seconds.
     */
    public long getTimeElapsed() {
        return timeElapsed;
    }

    /**
     * Calculates the score for the game.
     */
    private int calculateScore() {
        int timeScore = Math.round(10000f / (1 + getTimeElapsed()));
        int mistakesScore = Math.round(100f / (1 + getMistakesMade()));
        int score = timeScore + mistakesScore;
        if (difficulty == RealMain.Difficulty.HARD) score *= 10;
        return score;
    }

    /**
     * @return The score for the game.
     */
    public int getScore() {
        return score;
    }

    /**
     * Checks if the score for this game is a new highscore. If so, it adds it
     * to the highscore leaderboard.
     */
    public void checkHighscores() {
        // Count the number of highscores for the current difficulty
        int counter = 0;
        for (Highscore hs : highscores) {
            if (hs.getDifficulty() == difficulty) counter++;
        }

        // If there are less than 5 high scores, score immediately qualifies.
        if (counter < 5) {
            String userName = gameWindow.getHighscoreName();
            highscores.add(new Highscore(RealMain.GameType.CROSS_NUMBER_PUZZLE, difficulty, userName, score));

            // Sort the list
            highscores.sort((hs1, hs2) -> Integer.compare(hs2.getScore(), hs1.getScore()));
        }
        // Otherwise we need to check if the score is greater than the current highscores.
        else {
            for (Highscore hs : highscores) {
                if (hs.getDifficulty() == difficulty) {
                    if (score > hs.getScore()) {
                        highscores.remove(hs);
                        String userName = gameWindow.getHighscoreName();
                        highscores.add(new Highscore(RealMain.GameType.CROSS_NUMBER_PUZZLE, difficulty, userName, score));
                        break;
                    }
                }
            }

            // Sort the list
            highscores.sort((hs1, hs2) -> Integer.compare(hs2.getScore(), hs1.getScore()));
        }

        // Finally we need to save the highscores to file
        try {
            FileOutputStream fileOutStream = new FileOutputStream(RealMain.getBasePathForClass(RealMain.class) + File.separator + "CNP-highscores.dat");
            ObjectOutputStream objectOutStream = new ObjectOutputStream(fileOutStream);
            objectOutStream.writeObject(highscores);
            objectOutStream.close();
        }
        catch (Exception e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    /**
     * @return A list of highscores.
     */
    // DO NOT DELETE this method, despite seeming unused, it is in fact used by
    // DifficultyWindow.
    @SuppressWarnings("unused")
    public static ArrayList<Highscore> getHighscores() {
        return highscores;
    }

    /**
     * @return The difficulty.
     */
    public RealMain.Difficulty getDifficulty(){
        return difficulty;
    }

}
