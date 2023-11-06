/**
 * This class represents a single question in a Cross Number Puzzle. It contains
 * information about the question, such as the two operands that make up the
 * question as well as the operator used. It also stores the answer.
 */
public class CrossNumberPuzzleQuestion {
    private final int number;
    private final boolean goingAcross;  // true if question is going across, false if going down
    private final int answer;
    private final char operator;
    private final int operand1;
    private final int operand2;

    /**
     * Creates a new question.
     *
     * @param number The question number.
     * @param goingAcross True if going across, false if going down.
     * @param answer The answer of the question.
     * @param operator The operator.
     * @param operand1 The first operand.
     * @param operand2 The second operand.
     */
    public CrossNumberPuzzleQuestion(int number, boolean goingAcross, int answer, char operator, int operand1, int operand2) {
        this.number = number;
        this.goingAcross = goingAcross;
        this.answer = answer;
        this.operator = operator;
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    /**
     * @return The question in string form, e.g. operand1 operator operand2 = answer
     */
    @Override
    public String toString() {
        String s = "" + number;
        if (goingAcross) s += " across: ";
        else s += " down: ";
        s += operand1 + " " + operator + " " + operand2 + " = " + answer;
        return s;
    }

    /**
     * Returns the question with or without the answer, based on the answer parameter.
     *
     * @param includeAnswer True if the question is to include the answer, false otherwise.
     * @return The question - with or without the answer - in string form.
     */
    public String getQuestion(boolean includeAnswer) {
        String s = "" + number;

        // Add the gap and ensure the question will start at the same width
        // regardless of how many digits the question number is.
        if (number >= 10) s += ".    ";
        else s += ".      ";

        s += operand1 + " " + operator + " " + operand2 + " = ";

        if (includeAnswer) s += answer;
        else s += " ?";

        return s;
    }

    /**
     * @return True if the question is going across, false if going down.
     */
    public boolean isGoingAcross() {
        return goingAcross;
    }

}
