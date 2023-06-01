import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class UtilityButton extends JButton {
    public UtilityButton(String imageFileName, String toolTipText, ActionListener ActionListener) {
        super(new ImageIcon(imageFileName, toolTipText));
        this.setToolTipText(toolTipText);

        this.setPreferredSize(new Dimension(64, 64));

        this.addActionListener(ActionListener);
    }
}
