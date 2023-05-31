import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class UtilityButton extends JButton {
    public UtilityButton(String imageFileName, String toolTipText, String altText, ActionListener ActionListener) {
        super(new ImageIcon(imageFileName, altText));
        this.setToolTipText(toolTipText);

        this.setPreferredSize(new Dimension(64, 64));

        this.addActionListener(ActionListener);
    }
}
