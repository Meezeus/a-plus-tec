import java.util.HashMap;
import java.util.Map;

/**
 * This class is a helper class, designed to work alongside javafx nodes, in
 * particular their setStyle method. This method normally overrides previous
 * method calls. The helper class helps in this by storing the styles. A style
 * is a single CSS value, starting with "-fx". The helper class allows you to
 * add and remove styles, get the value of styles and also concatenate them into
 * a single string.
 */
public class StyleHelper {
    private final HashMap<String, String> styleMap = new HashMap<>();

    /**
     * Constructor that creates a blank Style Helper.
     */
    public StyleHelper() {}

    /**
     * Constructor that automatically adds one or more styles. Multiple styles
     * must be separated by semicolons (and optionally spaces). A single style
     * may end with a semicolon but does not have to.
     *
     * @param styles The style(s) to be added.
     */
    public StyleHelper(String styles) {
        addStyle(styles);
    }

    /**
     * Adds one or more styles. If one of the styles has already been added, its
     * value will be replaced. Multiple styles must be separated by semicolons
     * (and optionally spaces). A single style may end with a semicolon but does
     * not have to.
     *
     * @param style The style(s) to be added.
     */
    @SuppressWarnings("ExtractMethodRecommender")
    public void addStyle(String style) {
        int startIndex = 0;     // a counter for where a style beings.
        for (int i = 0; i < style.length(); i++) {
            // Once we reach a semicolon or the end of the string, we need to add a style.
            if (style.charAt(i) == ';' || i == style.length() - 1) {
                String singleStyle = null;  // this will be the single style being added
                // If we have reached the semicolon, we don't want to include it.
                if (style.charAt(i) == ';') {
                    singleStyle = style.substring(startIndex, i);
                }
                // If we have reached the last character, we want to include it.
                else if (i == style.length() - 1) {
                    singleStyle = style.substring(startIndex, i + 1);
                }
                // Add the style and its value to the map.
                assert singleStyle != null;
                styleMap.put(singleStyle.substring(0, singleStyle.indexOf(":") + 1), singleStyle.substring(singleStyle.indexOf(":") + 1));

                // Increase the startIndex to the next character after the semicolon/last character.
                startIndex = i + 1;
                // Check if startIndex is out of bounds, because the character was the last character in the string.
                if (startIndex >= style.length()) break;
                // Otherwise check if the semicolon is followed by a space, in which case skip it.
                else if (style.charAt(startIndex) == ' ') {
                    startIndex++;
                }
            }
        }
    }

    /**
     * Removes a single type of style.
     *
     * @param style The type of style to be removed, WITHOUT the colon or the value, e.g. "-fx-background-color".
     */
    @SuppressWarnings("unused")
    public void removeStyle(String style) {
        styleMap.remove(style +":");
    }

    /**
     * Replaces all the current styles with the new styles. Multiple styles must
     * be separated by semicolons (and optionally spaces). A single style may
     * end with a semicolon but does not have to.
     *
     * @param style The style(s) to be removed.
     */
    public void replaceStyles(String style) {
        styleMap.clear();
        addStyle(style);
    }

    /**
     * Combines all the styles to be applied to the node into a single string.
     * The style are separated by semicolons and spaces. The last style ends in
     * a semicolon.
     *
     * @return A String with all the styles that are to be applied to the node.
     */
    public String getStyle() {
        StringBuilder styleString = new StringBuilder();
        for (Map.Entry<String, String> pair : styleMap.entrySet()) {
            styleString.append(pair.getKey()).append(pair.getValue()).append("; ");
        }
        return styleString.toString();
    }

}
