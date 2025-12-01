package ui;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.RESET_BG_COLOR;
import static ui.EscapeSequences.RESET_TEXT_ITALIC;

public class Formatting {
    public static final String RESET = RESET_TEXT_COLOR + RESET_BG_COLOR + RESET_TEXT_ITALIC;
    public static final String ERROR_TEXT = SET_TEXT_COLOR_LIGHT_RED + SET_TEXT_ITALIC;
    public static final String LIGHT_BLUE_TEXT = SET_TEXT_COLOR_LIGHT_BLUE;
    public static final String BLUE_TEXT = SET_TEXT_COLOR_BLUE;
}
