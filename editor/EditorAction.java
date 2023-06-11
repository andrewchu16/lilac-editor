package editor;
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
