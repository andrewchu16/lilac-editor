import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import editor.Canvas;
import editor.Const;
import editor.Tool;
import window.EditorMenuBar;
import window.Settings;
import window.TabManager;
import window.ToolBar;
import window.ToolButton;
import window.UtilityButton;

public class LilacEditor {
    private JFrame frame;
    private Settings settings;
    private EditorMenuBar menuBar;
    private ToolBar toolBar;
    private TabManager tabManager;
    private Tool tool;

    public LilacEditor() {
        this.tool = new Tool(Const.SELECT_TOOL_TYPE);
        this.setupWindow();
        this.setupMenuBar();
        this.setupToolBar();
        this.setupTabManager();
        this.loadSettings();
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

        UIManager.put("TextField.inactiveBackground", UIManager.getColor("TextField.background"));

        this.frame = new JFrame(Const.TITLE_NAME);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setResizable(true);
        this.frame.setLayout(new BorderLayout());
    }

    private void setupMenuBar() {
        this.menuBar = new EditorMenuBar();
        this.frame.setJMenuBar(menuBar);

        this.menuBar.addMenuItem(Const.FILE_MENU_TEXT, Const.NEW_FILE_COMMAND, Const.NEW_FILE_KEYSTROKE, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.out.println(Const.NEW_FILE_COMMAND);
                tabManager.addCanvas(new Canvas(tool));
            }
        });

        this.menuBar.addMenuItem(Const.FILE_MENU_TEXT, Const.OPEN_FILE_COMMAND, Const.OPEN_FILE_KEYSTROKE, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.out.println(Const.OPEN_FILE_COMMAND);
            }
        });

        this.menuBar.addMenuItem(Const.FILE_MENU_TEXT, Const.CLOSE_FILE_COMMAND, Const.CLOSE_FILE_KEYSTROKE, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.out.println(Const.CLOSE_FILE_COMMAND);
                tabManager.remove(tabManager.getSelectedComponent());
            }
        });

        this.menuBar.addSeparator(Const.FILE_MENU_TEXT);

        this.menuBar.addMenuItem(Const.FILE_MENU_TEXT, Const.SAVE_FILE_COMMAND, Const.SAVE_FILE_KEYSTROKE, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                save();
            }
        });

        this.menuBar.addMenuItem(Const.FILE_MENU_TEXT, Const.SAVE_AS_COMMAND, Const.SAVE_AS_KEYSTROKE, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.out.println(Const.SAVE_AS_COMMAND);
            }
        });

        this.menuBar.addMenuItem(Const.FILE_MENU_TEXT, Const.EXPORT_COMMAND, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.out.println(Const.EXPORT_COMMAND);
            }
        });

        this.menuBar.addSeparator(Const.FILE_MENU_TEXT);

        this.menuBar.addMenuItem(Const.FILE_MENU_TEXT, Const.EXIT_COMMAND, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            }
        });

        this.menuBar.addMenuItem(Const.EDIT_MENU_TEXT, Const.UNDO_COMMAND, Const.UNDO_KEYSTROKE, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                undo();
            }
        });

        this.menuBar.addMenuItem(Const.EDIT_MENU_TEXT, Const.REDO_COMMAND, Const.REDO_KEYSTROKE, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                redo();
            }
        });

        this.menuBar.addSeparator(Const.EDIT_MENU_TEXT);

        this.menuBar.addMenuItem(Const.EDIT_MENU_TEXT, Const.DELETE_COMMAND, Const.DELETE_KEYSTROKE, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.out.println(Const.DELETE_COMMAND);
            }
        });

        this.menuBar.addMenuItem(Const.EDIT_MENU_TEXT, Const.DUPLICATE_COMMAND, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                duplicateSelected();
            }
        });

        this.menuBar.addSeparator(Const.EDIT_MENU_TEXT);

        this.menuBar.addMenuItem(Const.EDIT_MENU_TEXT, Const.COPY_COMMAND, Const.COPY_KEYSTROKE, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.out.println(Const.COPY_COMMAND);
            }
        });

        this.menuBar.addMenuItem(Const.EDIT_MENU_TEXT, Const.PASTE_COMMAND, Const.PASTE_KEYSTROKE, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.out.println(Const.PASTE_COMMAND);
            }
        });

        this.menuBar.addMenuItem(Const.VIEW_MENU_TEXT, Const.ZOOM_IN_COMMAND, Const.ZOOM_IN_KEYSTROKE, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                zoomIn();
            }
        });

        this.menuBar.addMenuItem(Const.VIEW_MENU_TEXT, Const.ZOOM_OUT_COMMAND, Const.ZOOM_OUT_KEYSTROKE, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                zoomOut();
            }
        });

        this.menuBar.addMenuItem(Const.VIEW_MENU_TEXT, Const.NEXT_TAB_COMMAND, Const.NEXT_TAB_KEYSTROKE, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                tabManager.swapToNextTab();
            }
        });

        this.menuBar.addMenuItem(Const.VIEW_MENU_TEXT, Const.PREV_TAB_COMMAND, Const.PREV_TAB_KEYSTROKE, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                tabManager.swapToPreviousTab();
            }
        });

        this.menuBar.setMnemonic(Const.FILE_MENU_TEXT, Const.FILE_MENU_MNEMONIC);
        this.menuBar.setMnemonic(Const.EDIT_MENU_TEXT, Const.EDIT_MENU_MNEMONIC);
        this.menuBar.setMnemonic(Const.VIEW_MENU_TEXT, Const.VIEW_MENU_MNEMONIC);
    }

    private void setupToolBar() {
        this.toolBar = new ToolBar("Tool Selector", this.tool);
        this.frame.add(this.toolBar, BorderLayout.WEST);

        this.toolBar.addUtilityButton(
            new UtilityButton(Const.UNDO_ICON_FILE_NAME, Const.UNDO_COMMAND, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    undo();
                }
            })
        );

        this.toolBar.addUtilityButton(
            new UtilityButton(Const.REDO_ICON_FILE_NAME, Const.REDO_COMMAND, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    redo();
                }
            })
        );

        this.toolBar.addUtilityButton(
            new UtilityButton(Const.SAVE_ICON_FILE_NAME, Const.SAVE_FILE_COMMAND, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    save();
                }
            })
        );

        this.toolBar.addUtilityButton(
            new UtilityButton(Const.DUPLICATE_ICON_FILE_NAME, Const.DUPLICATE_COMMAND, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    duplicateSelected();
                }
            })
        );

        this.toolBar.addUtilityButton(
            new UtilityButton(Const.ZOOM_IN_ICON_FILE_NAME, Const.ZOOM_IN_COMMAND, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    zoomIn();
                }
            })
        );

        this.toolBar.addUtilityButton(
            new UtilityButton(Const.ZOOM_OUT_ICON_FILE_NAME, Const.ZOOM_OUT_COMMAND, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    zoomOut();
                }
            })
        );

        this.toolBar.addToolButton(
            new ToolButton(Const.SELECT_ICON_FILE_NAME, Const.SELECT_TOOL_TYPE)
        );

        this.toolBar.addToolButton(
            new ToolButton(Const.CLASS_ICON_FILE_NAME, Const.CLASS_TOOL_TYPE)
        );

        this.toolBar.addToolButton(
            new ToolButton(Const.INTERFACE_ICON_FILE_NAME, Const.INTERFACE_TOOL_TYPE)
        );

        this.toolBar.addToolButton(
            new ToolButton(Const.INHERITS_ICON_FILE_NAME, Const.INHERITS_TOOL_TYPE)
        );

        this.toolBar.addToolButton(
            new ToolButton(Const.IMPLEMENTS_ICON_FILE_NAME, Const.IMPLEMENTS_TOOL_TYPE)
        );

        this.toolBar.addToolButton(
            new ToolButton(Const.AGGREGATE_ICON_FILE_NAME, Const.AGGREGATE_TOOL_TYPE)
        );

        this.toolBar.addToolButton(
            new ToolButton(Const.COMPOSED_ICON_FILE_NAME, Const.COMPOSED_TOOL_TYPE)
        );

        this.toolBar.setTool(Const.SELECT_TOOL_TYPE);
    }

    private void setupTabManager() {
        this.tabManager = new TabManager();

        this.tabManager.addCanvas(new Canvas(this.tool));
        this.frame.add(this.tabManager, BorderLayout.CENTER);
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
            @Override
            public void componentMoved(ComponentEvent event) {
                boolean isMaximized = (frame.getLocation().getX() < 0);
                if (!isMaximized) {
                    settings.setWindowLocation(frame.getLocation());
                }
            }

            @Override
            public void componentResized(ComponentEvent event) {
                // boolean isMaximized = (frame.getLocation().getX() < 0);
                boolean isMaximized = (frame.getExtendedState() & JFrame.MAXIMIZED_BOTH) != 0;
                if (!isMaximized) {
                    if (settings.getIsMaximized()) {
                        frame.setSize(settings.getWindowDimension());
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

    public boolean undo() {
        Canvas canvas = this.getSelectedCanvas();
        canvas.undo();
        return canvas.canUndo();
    }

    public boolean redo() {
        Canvas canvas = this.getSelectedCanvas();
        canvas.redo();
        return canvas.canRedo();
    }

    public void save() {
        Canvas canvas = this.getSelectedCanvas();
        if (!canvas.isSaved()) {
            canvas.save();
        }
    }

    public void duplicateSelected() {
        System.out.println(Const.DUPLICATE_COMMAND);
    }

    public void zoomIn() {
        Canvas canvas = this.getSelectedCanvas();
        canvas.zoomIn();
    }

    public void zoomOut() {
        Canvas canvas = this.getSelectedCanvas();
        canvas.zoomOut();
    }

    public Canvas getSelectedCanvas() {
        return this.tabManager.getSelectedCanvas();
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