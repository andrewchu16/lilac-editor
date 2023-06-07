package window;
import java.awt.Color;
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
        this.setBackground(Color.WHITE);
        this.setFocusable(false);
        this.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent event) {
                if (isSelected()) {
                    setBackground(Color.CYAN);
                } else {
                    setBackground(Color.WHITE);
                }
            }
        });
    }
}
