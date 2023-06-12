package editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import diagram.Arrow;
import diagram.ClassDiagram;
import diagram.Diagram;
import diagram.InterfaceDiagram;
import utility.Vector;

public class Canvas extends JScrollPane {
    private File file;
    private String title;
    private JViewport viewport;
    private JPanel innerPanel;
    private JLayer<JComponent> layer;
    private ZoomUI layerUI;

    private EditorActionHistory actionHistory;
    private EditorAction lastSavedAction;
    private Diagram selectedDiagram;
    private Arrow selectedArrow;
    private Tool tool;

    private int zoomLevelIndex;

    private static final double[] ZOOM_LEVELS = { 0.5, 0.75, 0.9, 1.0, 1.15, 1.5, 2.0 };
    private static final String DEFAULT_CANVAS_NAME = "./untitled.canvas";
    private static final Dimension DEFAULT_CANVAS_SIZE = new Dimension(2100, 2100);

    public Canvas(Tool tool) {
        this(DEFAULT_CANVAS_NAME, tool);
    }

    public Canvas(String filePath, Tool tool) {
        super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.file = new File(filePath);
        this.title = this.getFileName();
        this.actionHistory = new EditorActionHistory();
        this.lastSavedAction = null;
        this.selectedDiagram = null;
        this.selectedArrow = null;
        this.tool = tool;
        this.zoomLevelIndex = Arrays.binarySearch(ZOOM_LEVELS, 1.0);
        
        this.getHorizontalScrollBar().setUnitIncrement(4);
        this.getVerticalScrollBar().setUnitIncrement(4);
        
        this.layerUI = new ZoomUI();
        this.innerPanel = new JPanel();
        this.innerPanel.setPreferredSize(DEFAULT_CANVAS_SIZE);
        this.innerPanel.setLayout(null);
        this.innerPanel.setBackground(new Color(243, 243, 243));
        this.layer = new JLayer<JComponent>(this.innerPanel, this.layerUI);

        this.viewport = new JViewport();
        this.setViewport(viewport);
        this.viewport.add(this.layer);

        // Event Listeners
        this.innerPanel.addMouseListener(CANVAS_MOUSE_LISTENER);
    }

    public String getFilePath() {
        return this.file.getAbsolutePath();
    }

    public String getFileName() {
        return this.file.getName();
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean canUndo() {
        return this.actionHistory.canUndo();
    }

    public boolean canRedo() {
        return this.actionHistory.canRedo();
    }

    public void save() {
        this.lastSavedAction = this.actionHistory.getLastPerformedAction();
    }

    public boolean isSaved() {
        return this.lastSavedAction == this.actionHistory.getLastPerformedAction();
    }

    public boolean undo() {
        this.actionHistory.undo();
        return this.actionHistory.canUndo();
    }

    public boolean redo() {
        this.actionHistory.redo();
        return this.actionHistory.canRedo();
    }

    public boolean canZoomIn() {
        return this.zoomLevelIndex < ZOOM_LEVELS.length - 1;
    }

    public boolean canZoomOut() {
        return this.zoomLevelIndex > 0;
    }

    public void zoomIn() {
        if (this.canZoomIn()) {
            this.zoomLevelIndex++;
            this.updateCanvas();
        }
    }

    public void zoomOut() {
        if (this.canZoomOut()) {
            this.zoomLevelIndex--;
            this.updateCanvas();
        }
    }

    private void updateCanvas() {
        this.layerUI.setZoom(this.getZoomLevel());
        this.revalidate();
        this.repaint();
    }

    public void addDiagram(Diagram diagram) {
        diagram.addMouseListener(DIAGRAM_MOUSE_LISTENER);
        diagram.addFocusListener(DIAGRAM_FOCUS_LISTENER);
        this.innerPanel.add(diagram);
        this.updateCanvas();
    }

    public void removeDiagram(Diagram diagram) {
        this.innerPanel.remove(diagram);
        this.updateCanvas();
    }

    public void addArrow(Arrow arrow) {
        Diagram startDiagram = arrow.getStartDiagram();
        Diagram endDiagram = arrow.getEndDiagram();

        if (startDiagram.hasArrow(arrow) || endDiagram.hasArrow(arrow)) {
            // An arrow between the two diagrams already exists.
            return;
        }

        arrow.addMouseListener(ARROW_MOUSE_LISTENER);
        this.innerPanel.add(arrow);
        startDiagram.addArrow(arrow);
        endDiagram.addArrow(arrow);
        this.updateCanvas();
    }

    public void removeArrow(Arrow arrow) {
        Diagram startDiagram = arrow.getStartDiagram();
        Diagram endDiagram = arrow.getEndDiagram();

        startDiagram.removeArrow(arrow);
        endDiagram.removeArrow(arrow);
        this.innerPanel.remove(arrow);
        this.updateCanvas();
    }

    public void export() {
        BufferedImage image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.createGraphics();
        this.paint(graphics);

        File outputFile = new File(this.getFileName() + ".jpg");
        try {
            ImageIO.write(image, "jpg", outputFile);
        } catch (IOException exception) {
            System.err.println("Error exporting image.");
        }
    }

    public void removeSelected() {
        EditorAction action = new DeleteAction(this.selectedArrow, this.selectedDiagram);
        this.actionHistory.add(action);

        if (this.selectedDiagram != null) {
            this.removeDiagram(this.selectedDiagram);
        }

        if (this.selectedArrow != null) {
            this.removeArrow(this.selectedArrow);
        }

    }

    public double getZoomLevel() {
        return ZOOM_LEVELS[this.zoomLevelIndex];
    }

    public final MouseAdapter CANVAS_MOUSE_LISTENER = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent event) {
            requestFocus();
            selectedDiagram = null;
            selectedArrow = null;
            if (tool.equals(Const.CLASS_TOOL_TYPE)) {
                Point pos = event.getPoint();
                selectedDiagram = new ClassDiagram("New Class", pos);
                addDiagram(selectedDiagram);

                CreateDiagramAction action = new CreateDiagramAction(selectedDiagram);
                actionHistory.add(action);
            } else if (tool.equals(Const.INTERFACE_TOOL_TYPE)) {
                Point pos = event.getPoint();
                selectedDiagram = new InterfaceDiagram("New Interface", pos);
                addDiagram(selectedDiagram);

                CreateDiagramAction action = new CreateDiagramAction(selectedDiagram);
                actionHistory.add(action);
            }
        }
    };

    public final MouseAdapter DIAGRAM_MOUSE_LISTENER = new MouseAdapter() {
        private Point mouseStartPos = new Point();

        @Override
        public void mousePressed(MouseEvent event) {
            Component diagramChild = event.getComponent();
            selectedDiagram = (Diagram) diagramChild.getParent();
            selectedArrow = null;
            diagramChild.requestFocus();
            if (tool.equals(Const.SELECT_TOOL_TYPE)) {
                this.mouseStartPos = SwingUtilities.convertPoint(diagramChild, event.getPoint(), innerPanel);
            }
        }

        @Override
        public void mouseReleased(MouseEvent event) {
            Component diagramChild = event.getComponent();
            Point mouseEndPos = SwingUtilities.convertPoint(diagramChild, event.getPoint(), innerPanel);
            Component endDiagram = innerPanel.getComponentAt(mouseEndPos);

            if (tool.equals(Const.SELECT_TOOL_TYPE)) {
                Vector changeInPos = Vector.difference(new Vector(mouseEndPos), new Vector(this.mouseStartPos));
                selectedDiagram.shiftPos(changeInPos);
                EditorAction action = new MoveDiagramAction(selectedDiagram, changeInPos);
                actionHistory.add(action);
            } else if (tool.equals(Const.INHERITS_TOOL_TYPE)) {
                if (endDiagram instanceof Diagram) {
                    Arrow arrow = new Arrow(selectedDiagram, (Diagram) endDiagram, Arrow.SOLID, Arrow.TRIANGLE_END);
                    addArrow(arrow);
                    EditorAction action = new CreateArrowAction(arrow);
                    actionHistory.add(action);
                }
            } else if (tool.equals(Const.IMPLEMENTS_TOOL_TYPE)) {
                if (endDiagram instanceof Diagram) {
                    Arrow arrow = new Arrow(selectedDiagram, (Diagram) endDiagram, Arrow.DASHED, Arrow.TRIANGLE_END);
                    addArrow(arrow);
                    EditorAction action = new CreateArrowAction(arrow);
                    actionHistory.add(action);
                }
            } else if (tool.equals(Const.AGGREGATE_TOOL_TYPE)) {
                if (endDiagram instanceof Diagram) {
                    Arrow arrow = new Arrow(selectedDiagram, (Diagram) endDiagram, Arrow.SOLID, Arrow.LINE_DIAMOND_END);
                    addArrow(arrow);
                    EditorAction action = new CreateArrowAction(arrow);
                    actionHistory.add(action);
                }
            } else if (tool.equals(Const.COMPOSED_TOOL_TYPE)) {
                if (endDiagram instanceof Diagram) {
                    Arrow arrow = new Arrow(selectedDiagram, (Diagram) endDiagram, Arrow.SOLID, Arrow.FILL_DIAMOND_END);
                    addArrow(arrow);
                    EditorAction action = new CreateArrowAction(arrow);
                    actionHistory.add(action);
                }
            }
        }
    };

    public final MouseListener ARROW_MOUSE_LISTENER = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent event) {
            Arrow arrow = (Arrow) event.getComponent();
            selectedArrow = arrow;
        }
    };

    public final FocusListener DIAGRAM_FOCUS_LISTENER = new FocusListener() {
        @Override
        public void focusGained(FocusEvent event) {
            Component diagramChild = event.getComponent();
            selectedDiagram = (Diagram) diagramChild.getParent();
            selectedArrow = null;
            selectedDiagram.setBorder(BorderFactory.createLineBorder(Color.CYAN, 2));
        }

        @Override
        public void focusLost(FocusEvent event) {
            Component diagramChild = event.getComponent();
            Diagram diagram = (Diagram) diagramChild.getParent();

            // Part of the diagram lost focus but another part is still enabled.
            if (diagram.isEnabled()) {
                return;
            }

            diagram.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            if (diagram.textChanged()) {
                EditorAction action = new EditDiagramTextAction(diagram);
                actionHistory.add(action);
            }
        }
        
    };

    public class DeleteAction implements EditorAction {
        private Arrow arrow;
        private Diagram diagram;

        public DeleteAction(Arrow arrow, Diagram diagram) {
            this.arrow = arrow;
            this.diagram = diagram;
        }

        @Override
        public void redo() {
            if (this.arrow != null) {
                removeArrow(this.arrow);
            }

            if (this.diagram != null) {
                removeDiagram(this.diagram);
            }
        }

        @Override
        public void undo() {
            if (this.arrow != null) {
                addArrow(this.arrow);
            }

            if (this.diagram != null) {
                addDiagram(this.diagram);
            }
        }
    }

    public class CreateArrowAction implements EditorAction {
        private Arrow arrow;

        public CreateArrowAction(Arrow arrow) {
            this.arrow = arrow;
        }

        @Override
        public void redo() {
            addArrow(this.arrow);
        }

        @Override
        public void undo() {
            removeArrow(this.arrow);
        }
    }

    public class CreateDiagramAction implements EditorAction {
        private Diagram diagram;

        public CreateDiagramAction(Diagram diagram) {
            this.diagram = diagram;
        }

        @Override
        public void redo() {
            addDiagram(this.diagram);
        }

        @Override
        public void undo() {
            removeDiagram(this.diagram);
        }
    }

    public class MoveDiagramAction implements EditorAction {
        private Diagram diagram;
        private Vector changeInPos;

        public MoveDiagramAction(Diagram diagram, Vector changeInPos) {
            this.changeInPos = changeInPos;
            this.diagram = diagram;
        }

        @Override
        public void redo() {
            this.diagram.shiftPos(this.changeInPos);
        }

        @Override
        public void undo() {
            this.diagram.shiftPos(Vector.scaled(this.changeInPos, -1));
        }
    }
    public class EditDiagramTextAction implements EditorAction {
        private Diagram diagram;
        private String oldTitleText;
        private String newTitleText;
        private String oldMethodText;
        private String newMethodText;
        private String oldPropertiesText;
        private String newPropertiesText;

        public EditDiagramTextAction(Diagram diagram) {
            this.diagram = diagram;
            this.oldTitleText = diagram.getLastTitle();
            this.newTitleText = diagram.getTitle();

            if (diagram instanceof InterfaceDiagram) {
                this.oldMethodText = ((InterfaceDiagram) diagram).getLastMethodText();
                this.newMethodText = ((InterfaceDiagram) diagram).getMethodText();
            }

            if (diagram instanceof ClassDiagram) {
                this.oldPropertiesText = ((ClassDiagram) diagram).getLastPropertiesText();
                this.newPropertiesText = ((ClassDiagram) diagram).getPropertiesText();
            }
        }

        @Override
        public void redo() {
            this.diagram.setTitle(this.newTitleText);

            if (this.diagram instanceof InterfaceDiagram) {
                ((InterfaceDiagram) this.diagram).setMethodText(this.newMethodText);
            }
            
            if (this.diagram instanceof ClassDiagram) {
                ((ClassDiagram) this.diagram).setPropertiesText(this.newPropertiesText);
            }
        }

        @Override
        public void undo() {
            this.diagram.setTitle(this.oldTitleText);
            
            if (this.diagram instanceof InterfaceDiagram) {
                ((InterfaceDiagram) this.diagram).setMethodText(this.oldMethodText);
            }
            
            if (this.diagram instanceof ClassDiagram) {
                ((ClassDiagram) this.diagram).setPropertiesText(this.oldPropertiesText);
            }
        }
    }
}
