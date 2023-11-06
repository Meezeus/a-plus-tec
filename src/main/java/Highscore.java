import java.io.Serializable;

/**
 * This class contains information about a highscore.
 */
public class Highscore implements Serializable {

    private final RealMain.GameType gameType;
    private final RealMain.Difficulty difficulty;
    private final String playerName;
    private final int score;

    /**
     * Creates the highscore object and sets all its variables.
     * @param gameType The type of game the highscore is for.
     * @param difficulty The difficulty the highscore is for.
     * @param playerName The name of the player who achieved the highscore.
     * @param score The score.
     */
    public Highscore(RealMain.GameType gameType, RealMain.Difficulty difficulty, String playerName, int score) {
        this.gameType = gameType;
        this.difficulty = difficulty;
        this.playerName = playerName;
        this.score = score;
    }

    /**
     * @return The difficulty
     */
    public RealMain.Difficulty getDifficulty() {
        return difficulty;
    }

    /**
     * @return The score
     */
    public int getScore() {
        return score;
    }

    /**
     * @return A string representation of the highscore with the game name, difficulty, player name and score.
     */
    @Override
    public String toString() {
        String s;
        // If the difficulty is MEDIUM, there is one less space on each side of
        // the word so that the entries still line up.
        if (difficulty == RealMain.Difficulty.MEDIUM) {
            s = gameType.getGameName() + "    " + difficulty.name() + "    " + playerName + "     " + score;
        }
        else {
            s = gameType.getGameName() + "     " + difficulty.name() + "     " + playerName + "     " + score;
        }
        return s;
    }

}
