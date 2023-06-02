import java.awt.Component;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            int duplicateCount = 1;
            while (this.indexOfTab(canvas.getTitle() + " (" + duplicateCount + ")") != -1) {
                duplicateCount++;
            }
            this.add(canvas.getTitle() + " (" + duplicateCount + ")", canvas);
        }
        this.setTabComponentAt(this.getTabCount() - 1, new CloseableTab(this, canvas));
    }

    @Override
    public void remove(int index) {
        Canvas canvas = (Canvas) this.getComponentAt(index);
        String tabName = ((CloseableTab) this.getTabComponentAt(index)).getName();
        super.remove(index);

        Pattern duplicateNamePattern = Pattern.compile("\\((\\d+)\\)$");
        Matcher matcher = duplicateNamePattern.matcher(tabName);
        if (!duplicateNamePattern.matcher(canvas.getTitle()).find() && matcher.find()) {
            int duplicateCount = Integer.parseInt(matcher.group(1)) + 1;
            int tabIndex;

            while ((tabIndex = this.indexOfTab(canvas.getTitle() + " (" + duplicateCount + ")")) != -1) {
                this.setTitleAt(tabIndex, canvas.getTitle() + " (" + (duplicateCount - 1) + ")");
                duplicateCount++;
            }
        }
    }
}
