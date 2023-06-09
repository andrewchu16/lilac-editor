package window;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ToolButton extends JRadioButton {
    public ToolButton(String imageFileName, String toolText) {
        super(new ImageIcon(imageFileName));
        this.setText(toolText);
        this.setToolTipText(toolText);
        this.setActionCommand(toolText);
        this.setFocusable(false);
        this.setBorderPainted(true);
        this.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

        this.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent event) {
                if (isSelected()) {
                    setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
                } else {
                    setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
                }
            }
        });
    }
}
