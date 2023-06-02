import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class ToolBar extends JToolBar {
    private JPanel utilityButtonsPanel;
    private JPanel toolButtonsPanel;
    private ButtonGroup toolButtonGroup;

    public ToolBar(String name) {
        super(name);
        this.setFloatable(false);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        
        this.utilityButtonsPanel = new JPanel(new GridLayout(3, 2, 4, 4));
        // this.utilityButtonsPanel.setMinimumSize(new Dimension(132, 136));

        this.toolButtonsPanel = new JPanel(new GridLayout(0, 1, 0, 4));
        this.toolButtonGroup = new ButtonGroup();

        this.add(new JLabel("Utilities", JLabel.CENTER));
        this.add(new JScrollPane(utilityButtonsPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        this.add(new JLabel("Tools", JLabel.CENTER));
        this.add(new JScrollPane(toolButtonsPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
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
