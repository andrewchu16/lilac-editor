import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

public class LilacEditor {
    private JFrame frame;
    private Settings settings;

    public LilacEditor() {
        this.frame = new JFrame(Const.TITLE_NAME);
        this.settings = new Settings(Const.SETTINGS_FILE_NAME);

        if (!this.settings.load()) {
            this.frame.setLocation(Const.DEFAULT_LOCATION);
            this.settings.setWindowDimension(Const.DEFAULT_DIMENSION);
        }

        this.frame.getContentPane().setPreferredSize(this.settings.getWindowDimension());
        this.frame.setLocation(this.settings.getWindowLocation());
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setResizable(true);

        this.frame.addComponentListener(new ComponentAdapter() {
            public void componentMoved(ComponentEvent event) {
                settings.setWindowLocation(frame.getLocation());
            }

            public void componentResized(ComponentEvent event) {
                settings.setWindowDimension(frame.getSize());
                // settings.setWindowDimension(getContentPane().getSize());
            }
        });

        this.frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                settings.save();
            }
        });
    }

    public void run() {
        this.frame.pack();
        this.frame.setVisible(true);
    }

    public static void main(String[] args) {
        LilacEditor editor = new LilacEditor();
        editor.run();
    }
}