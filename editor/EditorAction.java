package editor;

/**
 * This interface represents an object that can perform an undoable action on the canvas of the editor.
 * @author Andrew Chu
 * @version June 2023
 */
public interface EditorAction {
    /**
     * Performs the action.
     * @return
     */
    public void redo();

    /**
     * Undoes the action once performed.
     * @return
     */
    public void undo();
}
