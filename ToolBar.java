import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Label;

public class ToolBar extends JToolBar {
    private JPanel utilityButtonsPanel;
    private JPanel toolButtonsPanel;
    private ButtonGroup toolButtonGroup;

    public ToolBar(String name) {
        super(name);
        this.setFloatable(false);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        this.utilityButtonsPanel = new JPanel(new GridLayout(0, 2, 4, 4));
        this.toolButtonsPanel = new JPanel(new GridLayout(0, 1, 0, 4));
        this.toolButtonGroup = new ButtonGroup();

        this.add(new Label("Utilities", Label.CENTER));
        this.add(utilityButtonsPanel);
        this.add(Box.createRigidArea(new Dimension(160, 80)));
        this.add(new Label("Tools", Label.CENTER));
        this.add(toolButtonsPanel);
        this.add(Box.createVerticalStrut(1000));
    }

    public void addUtilityButton(UtilityButton button) {
        this.utilityButtonsPanel.add(button);
    }

    public void addToolButton(ToolButton button) {
        this.toolButtonGroup.add(button);
        this.toolButtonsPanel.add(button);
    }

    public String getTool() {
        return this.toolButtonGroup.getSelection().getActionCommand();
    }

    public void setTool(String tool) {
        for (Component comp: this.toolButtonsPanel.getComponents()) {
            if (comp instanceof ToolButton) {
                ToolButton button = (ToolButton) comp;

                if (button.getActionCommand().equals(tool)) {
                    button.setSelected(true);
                    return;
                }
            }
        }
    }
}
