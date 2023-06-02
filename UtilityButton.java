import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class UtilityButton extends JButton {
    public static final int SIZE = 64;
    public UtilityButton(String imageFileName, String toolTipText, ActionListener ActionListener) {
        super(new ImageIcon(imageFileName, toolTipText));
        this.setToolTipText(toolTipText);
        this.setPreferredSize(new Dimension(SIZE, SIZE));
        this.addActionListener(ActionListener);
    }
}
