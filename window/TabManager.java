package window;
import java.awt.Component;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTabbedPane;

import editor.Canvas;

public class TabManager extends JTabbedPane {
    public TabManager() {
        super();
        this.setOpaque(false);
        this.requestFocus();
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
            this.addTab(canvas.getTitle(), canvas);
        } else {
            int duplicateCount = 1;
            while (this.indexOfTab(canvas.getTitle() + " (" + duplicateCount + ")") != -1) {
                duplicateCount++;
            }
            this.addTab(canvas.getTitle() + " (" + duplicateCount + ")", canvas);
        }
        this.setTabComponentAt(this.getTabCount() - 1, new CloseableTab(this, canvas));
    }

    @Override
    public Component add(Component component) {
        if (component instanceof Canvas) {
            this.addCanvas((Canvas) component);
            return component;
        } else {
            return super.add(component);
        }
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

    public void swapToNextTab() {
        int index = this.getSelectedIndex();
        int newIndex = (index + 1) % this.getTabCount();
        this.setSelectedIndex(newIndex);
    }

    public void swapToPreviousTab() {
        int index = this.getSelectedIndex();
        int newIndex = (index + this.getTabCount() - 1) % this.getTabCount();
        this.setSelectedIndex(newIndex);
    }
}
