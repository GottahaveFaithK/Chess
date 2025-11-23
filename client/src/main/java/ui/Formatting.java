package ui;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.RESET_BG_COLOR;
import static ui.EscapeSequences.RESET_TEXT_ITALIC;

public class Formatting {
    public static final String reset = RESET_TEXT_COLOR + RESET_BG_COLOR + RESET_TEXT_ITALIC;
    public static final String errorText = SET_TEXT_COLOR_LIGHT_RED + SET_TEXT_ITALIC;
    public static final String blueText = SET_TEXT_COLOR_LIGHT_BLUE;
}
