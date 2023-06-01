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
        this.add(canvas);
    }
}
