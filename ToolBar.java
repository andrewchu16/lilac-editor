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
    private JPanel utilityButtons;
    private JPanel toolButtons;
    private ButtonGroup toolButtonGroup;

    public ToolBar(String name) {
        super(name);
        this.setFloatable(false);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.utilityButtons = new JPanel(new GridLayout(0, 2, 4, 4));
        this.toolButtons = new JPanel(new GridLayout(0, 1));
        this.toolButtonGroup = new ButtonGroup();

        this.add(new Label("Utilities", Label.CENTER));
        this.add(utilityButtons);
        this.add(Box.createRigidArea(new Dimension(160, 100)));
        this.add(new Label("Tools", Label.CENTER));
        this.add(toolButtons);
        this.add(Box.createVerticalStrut(1000));
    }

    public void addUtilityButton(UtilityButton button) {
        this.utilityButtons.add(button);
    }

    public void addToolButton(ToolButton button) {
        this.toolButtonGroup.add(button);
        this.toolButtons.add(button);
    }

    public String getTool() {
        return this.toolButtonGroup.getSelection().getActionCommand();
    }

    public void setTool(String tool) {
        for (Component comp: this.toolButtons.getComponents()) {
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
