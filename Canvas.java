import java.io.File;

import javax.swing.JScrollPane;

public class Canvas extends JScrollPane {
    private File file;
    private String title;

    public Canvas() {
        super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.file = new File("./untitled.canvas");
        this.title = this.getFileName();
    }

    public Canvas(String filePath) {
        this.file = new File(filePath);
    }

    public String getFilePath() {
        return this.file.getAbsolutePath();
    }

    public String getFileName() {
        return this.file.getName();
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean canUndo() {
        return false;
    }

    public boolean canRedo() {
        return false;
    }

    public void save() {

    }

    public boolean isSaved() {
        return true;
    }

    public boolean undo() {
        return true;
    }

    public boolean redo() {
        return true;
    }
}
