import javax.swing.ImageIcon;
import javax.swing.JButton;

public class ToolButton extends JButton {
    public ToolButton(String imageFileName, String toolText) {
        super(new ImageIcon(imageFileName, toolText));
        this.setText(toolText);
        this.setToolTipText(toolText);
        this.setActionCommand(toolText);
    }
}
