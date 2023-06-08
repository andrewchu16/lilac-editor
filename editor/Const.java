package editor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;

public final class Const {
    public static final String SETTINGS_FILE_NAME = "settings.txt";

    // Window settings
    public static final String TITLE_NAME = "Lilac Editor";
    public static final Dimension DEFAULT_DIMENSION = new Dimension(800, 800);
    public static final Point DEFAULT_LOCATION = new Point(100, 100);
    public static final boolean DEFAULT_IS_MAXIMIZED = false;

    // Menu Mnemonics
    public static final int FILE_MENU_MNEMONIC = KeyEvent.VK_F;
    public static final int EDIT_MENU_MNEMONIC = KeyEvent.VK_E;
    public static final int VIEW_MENU_MNEMONIC = KeyEvent.VK_V;

    // Command Keybinds
    public static final String NEW_FILE_KEYSTROKE = "ctrl N";
    public static final String OPEN_FILE_KEYSTROKE = "ctrl O";
    public static final String CLOSE_FILE_KEYSTROKE = "ctrl W";
    public static final String SAVE_FILE_KEYSTROKE = "ctrl S";
    public static final String SAVE_AS_KEYSTROKE = "ctrl shift S";
    public static final String UNDO_KEYSTROKE = "ctrl Z";
    public static final String REDO_KEYSTROKE = "ctrl Y";
    public static final String DELETE_KEYSTROKE = "DELETE";
    public static final String COPY_KEYSTROKE = "ctrl C";
    public static final String PASTE_KEYSTROKE = "ctrl V";
    public static final String ZOOM_IN_KEYSTROKE = "ctrl EQUALS";
    public static final String ZOOM_OUT_KEYSTROKE = "ctrl MINUS";
    public static final String NEXT_TAB_KEYSTROKE = "ctrl TAB";
    public static final String PREV_TAB_KEYSTROKE = "ctrl shift TAB";

    // Menu Text
    public static final String FILE_MENU_TEXT = "File";
    public static final String EDIT_MENU_TEXT = "Edit";
    public static final String VIEW_MENU_TEXT = "View";

    // Command Text
    public static final String NEW_FILE_COMMAND = "New File";
    public static final String OPEN_FILE_COMMAND = "Open";
    public static final String CLOSE_FILE_COMMAND = "Close";
    public static final String SAVE_FILE_COMMAND = "Save";
    public static final String SAVE_AS_COMMAND = "Save As";
    public static final String EXPORT_COMMAND = "Export";
    public static final String EXIT_COMMAND = "Exit";

    public static final String UNDO_COMMAND = "Undo";
    public static final String REDO_COMMAND = "Redo";
    public static final String DELETE_COMMAND = "Delete";
    public static final String DUPLICATE_COMMAND = "Duplicate";
    public static final String COPY_COMMAND = "Copy";
    public static final String PASTE_COMMAND = "Paste";

    public static final String ZOOM_IN_COMMAND = "Zoom In";
    public static final String ZOOM_OUT_COMMAND = "Zoom Out";
    public static final String NEXT_TAB_COMMAND = "Next Tab";
    public static final String PREV_TAB_COMMAND = "Previous Tab";


    // Tool Text
    public static final String SELECT_TOOL_TYPE = "Select";
    public static final String CLASS_TOOL_TYPE = "Class";
    public static final String INTERFACE_TOOL_TYPE = "Interface"; 
    public static final String INHERITS_TOOL_TYPE = "Inherits from";
    public static final String IMPLEMENTS_TOOL_TYPE = "Implements interface";
    public static final String AGGREGATE_TOOL_TYPE = "Aggregate of";
    public static final String COMPOSED_TOOL_TYPE = "Composed of";

    // Utility Button Icons
    public static final String UNDO_ICON_FILE_NAME = "./assets/undo 64x64.png";
    public static final String REDO_ICON_FILE_NAME = "./assets/redo 64x64.png";
    public static final String SAVE_ICON_FILE_NAME = "./assets/save 64x64.png";
    public static final String DUPLICATE_ICON_FILE_NAME = "./assets/duplicate 64x64.png";
    public static final String ZOOM_IN_ICON_FILE_NAME = "./assets/zoom-in 64x64.png";
    public static final String ZOOM_OUT_ICON_FILE_NAME = "./assets/zoom-out 64x64.png";

    // Tool Button Icons
    public static final String SELECT_ICON_FILE_NAME = "./assets/undo 64x64.png";
    public static final String CLASS_ICON_FILE_NAME = "./assets/undo 64x64.png";
    public static final String INTERFACE_ICON_FILE_NAME = "./assets/undo 64x64.png";
    public static final String INHERITS_ICON_FILE_NAME = "./assets/undo 64x64.png";
    public static final String IMPLEMENTS_ICON_FILE_NAME = "./assets/undo 64x64.png";
    public static final String AGGREGATE_ICON_FILE_NAME = "./assets/undo 64x64.png";
    public static final String COMPOSED_ICON_FILE_NAME = "./assets/undo 64x64.png";
}
