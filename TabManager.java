import java.awt.Component;

import javax.swing.JTabbedPane;

public class TabManager extends JTabbedPane {
    public TabManager() {
        super();
    }

    public Canvas getSelectedCanvas() {
        Component component = getSelectedComponent();

        if (component instanceof Canvas) {
            return (Canvas) component;
        }

        return null;
    }
}
