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

        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setResizable(true);

        this.loadSettings();
    }

    private void loadSettings() {
        if (!this.settings.load()) {
            this.settings.setWindowLocation(Const.DEFAULT_LOCATION);
            this.settings.setWindowDimension(Const.DEFAULT_DIMENSION);
            this.settings.setIsMaximized(Const.DEFAULT_IS_MAXIMIZED);
        }
        
        this.frame.setLocation(this.settings.getWindowLocation());
        this.frame.getContentPane().setPreferredSize(this.settings.getWindowDimension());

        if (this.settings.getIsMaximized()) {
            this.frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }

        this.frame.addComponentListener(new ComponentAdapter() {
            public void componentMoved(ComponentEvent event) {
                boolean isMaximized = (frame.getLocation().getX() < 0);
                if (!isMaximized) {
                    settings.setWindowLocation(frame.getLocation());
                }
            }

            public void componentResized(ComponentEvent event) {
                boolean isMaximized = (frame.getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH;
                if (!isMaximized) {
                    if (settings.getIsMaximized()) {
                        frame.pack();
                    }
                    settings.setWindowDimension(frame.getSize());
                    // settings.setWindowDimension(getContentPane().getSize());
                }

                settings.setIsMaximized(isMaximized);
            }
        });

        this.frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                settings.save();
            }
        });
    }

    public void run() {
        if (!this.settings.getIsMaximized()) {
            this.frame.pack();
        }
        this.frame.setVisible(true);
    }

    public static void main(String[] args) {
        LilacEditor editor = new LilacEditor();
        editor.run();
    }
}