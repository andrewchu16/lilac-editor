import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class LilacEditor {
    private JFrame frame;
    private Settings settings;
    private ToolBar toolBar;
    private TabManager tabManager;
    private EditorMenuBar menuBar;

    public LilacEditor() {
        this.setupWindow();
        this.loadSettings();
        this.setupMenuBar();
    }

    private void setupWindow() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }

        this.frame = new JFrame(Const.TITLE_NAME);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setResizable(true);
        this.frame.setLayout(new BorderLayout());
    }

    private void loadSettings() {
        this.settings = new Settings(Const.SETTINGS_FILE_NAME);

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

    private void setupMenuBar() {
        this.menuBar = new EditorMenuBar();
        this.frame.setJMenuBar(menuBar);

        this.menuBar.addMenuItem("File", "New File", "ctrl N", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.out.println("New file");
            }
        });

        this.menuBar.addMenuItem("File", "Open", "ctrl O", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.out.println("Open file");
            }
        });

        this.menuBar.addMenuItem("File", "Close", "ctrl W", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.out.println("Close file");
            }
        });

        this.menuBar.addSeparator("File");

        this.menuBar.addMenuItem("File", "Save", "ctrl S", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                Canvas canvas = tabManager.getSelectedCanvas();
                canvas.save();
            }
        });

        this.menuBar.addMenuItem("File", "Save As", "ctrl shift S", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.out.println("Save As");
            }
        });

        this.menuBar.addMenuItem("File", "Export", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.out.println("Export file");
            }
        });

        this.menuBar.addSeparator("File");

        this.menuBar.addMenuItem("File", "Exit", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            }
        });

        this.menuBar.addMenuItem("Edit", "Undo", "ctrl Z", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.out.println("Undo");
            }
        });

        this.menuBar.addMenuItem("Edit", "Redo", "ctrl Y", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.out.println("Redo");
            }
        });

        this.menuBar.addSeparator("Edit");

        this.menuBar.addMenuItem("Edit", "Delete", "DELETE", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.out.println("Delete");
            }
        });

        this.menuBar.addMenuItem("Edit", "Duplicate", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.out.println("Duplicate");
            }
        });

        this.menuBar.addSeparator("Edit");

        this.menuBar.addMenuItem("Edit", "Copy", "ctrl C", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.out.println("Copy");
            }
        });

        this.menuBar.addMenuItem("Edit", "Paste", "ctrl V", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.out.println("Paste");
            }
        });

        this.menuBar.addMenuItem("View", "Zoom In", "ctrl EQUALS", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.out.println("Zoom In");
            }
        });

        this.menuBar.addMenuItem("View", "Zoom Out", "ctrl MINUS", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.out.println("Zoom Out");
            }
        });

        this.menuBar.setMnemonic("File", KeyEvent.VK_F);
        this.menuBar.setMnemonic("Edit", KeyEvent.VK_E);
        this.menuBar.setMnemonic("View", KeyEvent.VK_V);
    }

    public void run() {
        if (!this.settings.getIsMaximized()) {
            this.frame.pack();
        }
        this.frame.setVisible(true);
        this.frame.requestFocusInWindow();
    }

    public static void main(String[] args) {
        LilacEditor editor = new LilacEditor();
        editor.run();
    }
}