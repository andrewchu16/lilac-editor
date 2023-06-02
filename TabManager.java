import java.awt.Component;

import javax.swing.JTabbedPane;

public class TabManager extends JTabbedPane {
    private ToolBar toolBar;

    public TabManager(ToolBar toolBar) {
        super();
        this.toolBar = toolBar;
    }

    public Canvas getSelectedCanvas() {
        Component component = getSelectedComponent();

        if (component instanceof Canvas) {
            return (Canvas) component;
        }

        return null;
    }

    public void addCanvas(Canvas canvas) {
        if (this.indexOfTab(canvas.getTitle()) == -1) {
            this.add(canvas.getTitle(), canvas);
        } else {
            int index = 1;
            while (this.indexOfTab(canvas.getTitle() + " (" + index + ")") != -1) {
                index++;
            }
            this.add(canvas.getTitle() + " (" + index + ")", canvas);
        }
        this.setTabComponentAt(this.getTabCount() - 1, new CloseableTab(this, canvas));
    }

    @Override
    public void remove(int index) {
        Canvas canvas = (Canvas) this.getComponentAt(index);
        String tabName = ((CloseableTab) this.getTabComponentAt(index)).getName();
        super.remove(index);

        String duplicateNameRegex = "\\(\\d+\\)$";
        if (!canvas.getTitle().matches(duplicateNameRegex) && tabName.matches(duplicateNameRegex)) {
            
        }
    }
}
