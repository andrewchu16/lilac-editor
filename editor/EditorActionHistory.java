package editor;
import java.util.Stack;
import java.util.Deque;
import java.util.LinkedList;

public class EditorActionHistory {
    public static final int HISTORY_LENGTH = 10;

    private Deque<EditorAction> past;
    private Stack<EditorAction> future;
    private EditorAction lastPerformedAction;

    public EditorActionHistory() {
        this.past = new LinkedList<EditorAction>();
        this.future = new Stack<EditorAction>();
        this.lastPerformedAction = null;
    }

    public void add(EditorAction action) {
        this.future.clear();
        this.past.addLast(action);
        
        while (this.past.size() > HISTORY_LENGTH) {
            this.past.removeFirst();
        }

        // Assume the action was already performed.
        this.lastPerformedAction = action;
    }

    public EditorAction undo() {
        if (!this.canUndo()) { 
            return null;
        }

        EditorAction curAction = this.past.removeLast();
        curAction.undoAction();
        this.future.push(curAction);

        if (this.past.isEmpty()) {
            this.lastPerformedAction = null;
        } else {
            this.lastPerformedAction = this.past.peek();
        }

        return curAction;
    }

    public EditorAction redo() {
        if (!this.canRedo()) {
            return null;
        }

        EditorAction curAction = this.future.pop();
        curAction.doAction();
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
