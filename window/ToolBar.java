package window;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import editor.Tool;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ToolBar extends JToolBar {
    private JPanel utilityButtonsPanel;
    private JPanel toolButtonsPanel;
    private ButtonGroup toolButtonGroup;
    private Tool tool;

    public ToolBar(String name, Tool toolReference) {
        super(name);
        this.setFloatable(false);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        
        this.utilityButtonsPanel = new JPanel(new GridLayout(3, 2, 4, 4));
        this.toolButtonsPanel = new JPanel(new GridLayout(0, 1, 0, 4));
        this.toolButtonGroup = new ButtonGroup();
        this.tool = toolReference;

        JScrollPane utilityScrollPane = new JScrollPane(utilityButtonsPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        utilityScrollPane.getVerticalScrollBar().setUnitIncrement(4);

        JScrollPane toolScrollPane = new JScrollPane(toolButtonsPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        toolScrollPane.getVerticalScrollBar().setUnitIncrement(4);

        this.add(new JLabel("Utilities", JLabel.CENTER));
        this.add(utilityScrollPane);
        this.add(new JLabel("Tools", JLabel.CENTER));
        this.add(toolScrollPane);
    }

    public void addUtilityButton(UtilityButton button) {
        this.utilityButtonsPanel.add(button);
    }

    public void addToolButton(ToolButton button) {
        this.toolButtonGroup.add(button);
        this.toolButtonsPanel.add(button);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                tool.setType(event.getActionCommand());
            }
            
        });
    }

    public String getTool() {
        return this.toolButtonGroup.getSelection().getActionCommand();
    }

    public void setTool(String toolType) {
        this.tool.setType(toolType);
        System.out.println(this.tool.getType());
        for (Component comp: this.toolButtonsPanel.getComponents()) {
            if (comp instanceof ToolButton) {
                ToolButton button = (ToolButton) comp;

                if (button.getActionCommand().equals(toolType)) {
                    button.setSelected(true);
                    return;
                }
            }
        }
    }
}
