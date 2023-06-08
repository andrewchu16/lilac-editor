package editor;
public interface EditorAction {
    /**
     * Performs the action.
     * @return
     */
    public void doAction();

    /**
     * Undoes the action once performed.
     * @return
     */
    public void undoAction();
}
