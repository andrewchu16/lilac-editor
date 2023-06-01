import javax.swing.JScrollPane;

public class Canvas extends JScrollPane {
    private String filePath;

    public Canvas() {

    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean canUndo() {
        return false;
    }

    public boolean canRedo() {
        return false;
    }

    public void save() {

    }

    public boolean undo() {
        return true;
    }

    public boolean redo() {
        return true;
    }
}
