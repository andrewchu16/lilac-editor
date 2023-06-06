package editor;
import java.util.Stack;
import java.util.Deque;
import java.util.LinkedList;

public class EditorActionHistory {
    public static final int HISTORY_LENGTH = 10;

    private Deque<EditorAction> past;
    private Stack<EditorAction> future;

    public EditorActionHistory() {
        this.past = new LinkedList<EditorAction>();
        this.future = new Stack<EditorAction>();
    }

    public void add(EditorAction action) {
        this.future.clear();
        this.past.addLast(action);
        
        while (this.past.size() > HISTORY_LENGTH) {
            this.past.removeFirst();
        }
    }

    public boolean undo() {
        if (!this.canUndo()) { 
            return false;
        }

        EditorAction curAction = this.past.pop();
        curAction.undoAction();
        this.future.push(curAction);

        return true;
    }

    public boolean redo() {
        if (!this.canRedo()) {
            return false;
        }

        EditorAction curAction = this.future.pop();
        curAction.doAction();
        this.past.push(curAction);

        return true;
    }

    public boolean canUndo() {
        return !this.past.isEmpty();
    }

    public boolean canRedo() {
        return !this.future.isEmpty();
    }
}
