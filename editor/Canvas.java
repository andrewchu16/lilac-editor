package editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

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
    private Tool tool;

    private int zoomLevelIndex;

    private static final double[] ZOOM_LEVELS = { 0.5, 0.75, 0.9, 1.0, 1.15, 1.5, 2.0 };
    private static final String DEFAULT_CANVAS_NAME = "./untitled.canvas";
    private static final Dimension DEFAULT_CANVAS_SIZE = new Dimension(1920, 1080);

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
        this.tool = tool;
        this.zoomLevelIndex = Arrays.binarySearch(ZOOM_LEVELS, 1.0);
        
        this.getHorizontalScrollBar().setUnitIncrement(4);
        this.getVerticalScrollBar().setUnitIncrement(4);
        
        this.layerUI = new ZoomUI();
        this.innerPanel = new JPanel();
        this.innerPanel.setPreferredSize(DEFAULT_CANVAS_SIZE);
        this.innerPanel.setLayout(null);
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
        this.export();
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

    public void moveDiagram(Vector changeInPos) {

    }

    public void export() {

    }

    public double getZoomLevel() {
        return ZOOM_LEVELS[this.zoomLevelIndex];
    }

    public final MouseAdapter CANVAS_MOUSE_LISTENER = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent event) {
            requestFocus();
            selectedDiagram = null;
            if (tool.getType().equals(Const.CLASS_TOOL_TYPE)) {
                Point pos = event.getPoint();
                selectedDiagram = new ClassDiagram("New Class", pos);
                addDiagram(selectedDiagram);

                CreateDiagramAction action = new CreateDiagramAction(selectedDiagram);
                actionHistory.add(action);
            } else if (tool.getType().equals(Const.INTERFACE_TOOL_TYPE)) {
                Point pos = event.getPoint();
                selectedDiagram = new InterfaceDiagram("New Inerface", pos);
                addDiagram(selectedDiagram);

                CreateDiagramAction action = new CreateDiagramAction(selectedDiagram);
                actionHistory.add(action);
            }
        }

        @Override
        public void mousePressed(MouseEvent event) {
        }

        @Override
        public void mouseReleased(MouseEvent event) {
        }
    };

    public final MouseMotionAdapter DIAGRAM_MOUSE_MOTION_LISTENER = new MouseMotionAdapter() {
        @Override
        public void mouseDragged(MouseEvent event) {
            
        }

        @Override
        public void mouseMoved(MouseEvent event) {
        }
    };

    public final MouseAdapter DIAGRAM_MOUSE_LISTENER = new MouseAdapter() {
        private Point mouseStartPos = new Point();

        @Override
        public void mousePressed(MouseEvent event) {
            Component diagramChild = event.getComponent();
            selectedDiagram = (Diagram) diagramChild.getParent();
            diagramChild.requestFocus();
            if (tool.equals(Const.SELECT_TOOL_TYPE)) {
                this.mouseStartPos = SwingUtilities.convertPoint(diagramChild, event.getPoint(), innerPanel);
            }
        }

        @Override
        public void mouseReleased(MouseEvent event) {
            Component diagramChild = event.getComponent();
            Diagram diagram = (Diagram) diagramChild.getParent();

            if (tool.equals(Const.SELECT_TOOL_TYPE)) {
                Point mouseEndPos = SwingUtilities.convertPoint(diagramChild, event.getPoint(), innerPanel);
                Vector changeInPos = Vector.difference(new Vector(mouseEndPos), new Vector(this.mouseStartPos));
                selectedDiagram.shiftPos(changeInPos);
                EditorAction action = new MoveDiagram(selectedDiagram, changeInPos);
                actionHistory.add(action);
            }
        }
    };

    public final FocusListener DIAGRAM_FOCUS_LISTENER = new FocusListener() {
        @Override
        public void focusGained(FocusEvent event) {
            Component diagramChild = event.getComponent();
            selectedDiagram = (Diagram) diagramChild.getParent();
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
            EditorAction action = new EditDiagramTextAction(diagram);
            actionHistory.add(action);
        }
        
    };

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

    public class MoveDiagram implements EditorAction {
        private Diagram diagram;
        private Vector changeInPos;

        public MoveDiagram(Diagram diagram, Vector changeInPos) {
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

    public class MoveDiagramsAction implements EditorAction {
        private ArrayList<Diagram> diagrams;
        private ArrayList<Vector> changeInPosList;

        public MoveDiagramsAction(ArrayList<Diagram> diagrams, ArrayList<Vector> changeInPosList) {
            this.diagrams = diagrams;
            this.changeInPosList = changeInPosList;
        }

        @Override
        public void redo() {
            int length = Math.min(diagrams.size(), changeInPosList.size());
            for (int i = 0; i < length; i++) {
                Vector changeInPos = changeInPosList.get(i);
                this.diagrams.get(i).shiftPos(changeInPos);
            }
        }

        @Override
        public void undo() {
            int length = Math.min(diagrams.size(), changeInPosList.size());
            for (int i = 0; i < length; i++) {
                Vector changeInPos = Vector.scaled(changeInPosList.get(i), -1);
                this.diagrams.get(i).shiftPos(changeInPos);
            }
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
