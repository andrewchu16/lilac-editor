import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Label;

public class ToolBar extends JToolBar {
    private JPanel utilityButtons;
    private JPanel toolButtons;

    public ToolBar(String name) {
        super(name);
        this.setFloatable(false);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.utilityButtons = new JPanel(new GridLayout(0, 2, 4, 4));
        this.toolButtons = new JPanel(new GridLayout(0, 1));

        this.add(new Label("Toolbox", Label.CENTER));
        this.add(utilityButtons);
        this.add(toolButtons);
        this.add(Box.createVerticalStrut(10000));
    }

    public void addUtilityButton(UtilityButton button) {
        this.utilityButtons.add(button);
    }

    public void addToolButton(ToolButton button) {
        this.toolButtons.add(button);
    }
}
