package editor;
import java.util.Stack;
import java.util.Deque;
import java.util.LinkedList;

/**
 * This class stores the actions committed on the editor's canvas. It tracks the history of actions and the current action after undoing or redoing actions.
 * @author Andrew Chu
 * @version June 2023
 */
public class EditorActionHistory {
    public static final int HISTORY_LENGTH = 10;

    private Deque<EditorAction> past;
    private Stack<EditorAction> future;
    private EditorAction lastPerformedAction;

    /**
     * This constructs an empty editor action history.
     */
    public EditorActionHistory() {
        this.past = new LinkedList<EditorAction>();
        this.future = new Stack<EditorAction>();
        this.lastPerformedAction = null;
    }

    /**
     * This adds an action to the editor history.
     * @param action The action to add.
     */
    public void add(EditorAction action) {
        this.future.clear();
        this.past.addLast(action);
        
        while (this.past.size() > HISTORY_LENGTH) {
            this.past.removeFirst();
        }

        // Assume the action was already performed.
        this.lastPerformedAction = action;
    }

    /**
     * This undoes an action if available.
     * @return The action just undone, null if no action.
     */
    public EditorAction undo() {
        if (!this.canUndo()) { 
            return null;
        }

        EditorAction curAction = this.past.removeLast();
        curAction.undo();
        this.future.push(curAction);

        if (this.past.isEmpty()) {
            this.lastPerformedAction = null;
        } else {
            this.lastPerformedAction = this.past.peek();
        }

        return curAction;
    }

    /**
     * This redoes an action if available.
     * @return The action just redone, null if no action.
     */
    public EditorAction redo() {
        if (!this.canRedo()) {
            return null;
        }

        EditorAction curAction = this.future.pop();
        curAction.redo();
        this.past.addLast(curAction);

        this.lastPerformedAction = curAction;

        return curAction;
    }

    public boolean canUndo() {
        return !this.past.isEmpty();
    }

    public boolean canRedo() {
        return !this.future.isEmpty();
    }

    public EditorAction getLastPerformedAction() {
        return this.lastPerformedAction;
    }
}
