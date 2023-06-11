package editor;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayer;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.plaf.LayerUI;

import diagram.Diagram;
import math.Vector;

public class Canvas extends JScrollPane {
    private File file;
    private String title;
    private JViewport viewport;
    private JPanel innerPanel;
    private JLayer<JPanel> layer;
    private LayerUI<JPanel> layerUI;

    private EditorActionHistory actionHistory;
    private EditorAction lastSavedAction;
    private ArrayList<Diagram> selectedDiagrams;
    private Tool tool;

    private Dimension originalSize;
    private int zoomLevelIndex;

    private static final double[] ZOOM_LEVELS = { 0.5, 0.75, 0.9, 1.0, 1.15, 1.5, 2.0 };
    private static final String DEFAULT_CANVAS_NAME = "./untitled.canvas";

    public Canvas(Tool tool) {
        this(DEFAULT_CANVAS_NAME, tool);
    }

    public Canvas(String filePath, Tool tool) {
        super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.file = new File(filePath);
        this.title = this.getFileName();
        this.actionHistory = new EditorActionHistory();
        this.lastSavedAction = null;
        this.selectedDiagrams = new ArrayList<Diagram>();
        this.tool = tool;
        this.zoomLevelIndex = Arrays.binarySearch(ZOOM_LEVELS, 1.0);
        this.originalSize = new Dimension(1800, 1200);

        this.getHorizontalScrollBar().setUnitIncrement(4);
        this.getVerticalScrollBar().setUnitIncrement(4);
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        this.layerUI = new ZoomLayerUI();
        this.innerPanel = new JPanel();
        this.innerPanel.setPreferredSize(this.originalSize);
        this.innerPanel.setLayout(null);
        this.layer = new JLayer<JPanel>(this.innerPanel, this.layerUI);

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
            this.repaint();
        }
    }

    public void zoomOut() {
        if (this.canZoomOut()) {
            this.zoomLevelIndex--;
            this.updateCanvas();
        }
    }

    private void updateCanvas() {
        double zoomLevel = this.getZoomLevel();
        this.innerPanel.setPreferredSize(new Dimension((int) (this.originalSize.getWidth() * zoomLevel),
                (int) (this.originalSize.getHeight() * zoomLevel)));
        this.innerPanel.revalidate();
        this.innerPanel.repaint();
        this.layer.revalidate();
        this.layer.repaint();
        this.viewport.revalidate();
        this.viewport.repaint();
        this.revalidate();
        this.repaint();
    }

    public void addDiagram(Diagram diagram) {
        diagram.addMouseListener(DIAGRAM_MOUSE_LISTENER);
        this.innerPanel.add(diagram);
        this.updateCanvas();
    }

    public void removeDiagram(Diagram diagram) {
        this.innerPanel.remove(diagram);
        this.updateCanvas();
    }

    public void moveDiagrams(Vector changeInPos) {
        for (Diagram diagram : this.selectedDiagrams) {
            diagram.shiftPos(changeInPos);
        }
    }

    public void export() {

    }

    public double getZoomLevel() {
        return ZOOM_LEVELS[this.zoomLevelIndex];
    }

    private Point adjustPointForZoom(Point point) {
        double zoomLevel = this.getZoomLevel();
        return new Point((int) (point.x / zoomLevel), (int) (point.y / zoomLevel));
    }

    public final MouseAdapter CANVAS_MOUSE_LISTENER = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent event) {
            requestFocus();
            System.out.println(event.getID());
            if (tool.getType().equals(Const.CLASS_TOOL_TYPE)) {
                Point pos = adjustPointForZoom(event.getPoint());
                Diagram diagram = new Diagram("HELP ME", pos);
                addDiagram(diagram);

                CreateDiagramAction action = new CreateDiagramAction(diagram);
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
        public void mouseClicked(MouseEvent event) {
        }

        @Override
        public void mousePressed(MouseEvent event) {
            if (tool.equals(Const.SELECT_TOOL_TYPE)) {
                Diagram diagram = (Diagram) event.getComponent();
                selectedDiagrams.add(diagram);
                this.mouseStartPos = event.getPoint();
            }
        }

        @Override
        public void mouseReleased(MouseEvent event) {
            Diagram diagram = (Diagram) event.getComponent();
            if (tool.equals(Const.SELECT_TOOL_TYPE)) {
                selectedDiagrams.remove(diagram);
            }
        }

        @Override
        public void mouseEntered(MouseEvent event) {
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

    public class MoveAction implements EditorAction {
        private Diagram diagram;
        private Vector changeInPos;

        public MoveAction(Diagram diagram, Vector newPos) {
            Vector oldPos = diagram.getPos();
            this.changeInPos = Vector.difference(newPos, oldPos);
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

    public class MoveDiagramAction implements EditorAction {
        private ArrayList<Diagram> diagrams;
        private ArrayList<Vector> changeInPosList;

        public MoveDiagramAction(ArrayList<Diagram> diagrams, ArrayList<Vector> changeInPosList) {
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

    public class EditDiagramAction implements EditorAction {
        private Diagram diagram;
        private String oldTitleText;
        private String newTitleText;

        public EditDiagramAction(Diagram diagram, String oldText, String newText) {
            this.diagram = diagram;
            this.oldTitleText = oldText;
            this.newTitleText = newText;
        }

        @Override
        public void redo() {
            this.diagram.setTitle(this.newTitleText);
        }

        @Override
        public void undo() {
            this.diagram.setTitle(this.oldTitleText);
        }
    }

    public class ZoomLayerUI extends LayerUI<JPanel> {
        private boolean dispatchingMode;

        public ZoomLayerUI() {
            super();
            this.dispatchingMode = false;
        }

        @Override

        public void installUI(JComponent component) {
            super.installUI(component);
            JLayer<JPanel> layer = (JLayer<JPanel>) component;
            layer.setLayerEventMask(AWTEvent.MOUSE_EVENT_MASK & 0);
        }

        @Override
        public void paint(Graphics graphics, JComponent component) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.scale(getZoomLevel(), getZoomLevel());
            super.paint(g2, component);
            g2.dispose();
        }

        @Override
        public void uninstallUI(JComponent component) {
            JLayer<JPanel> layer = (JLayer<JPanel>) component;
            layer.setLayerEventMask(0);
            super.uninstallUI(component);
        }

        @Override
        public void eventDispatched(AWTEvent event, final JLayer<? extends JPanel> layer) {
            if (event instanceof MouseEvent) {
                MouseEvent mouseEvent = (MouseEvent) event;

                if (!dispatchingMode && mouseEvent.getID() == MouseEvent.MOUSE_CLICKED) {
                    // Process an original mouse event
                    dispatchingMode = true;
                    try {
                        redispatchMouseEvent(mouseEvent, layer);
                    } finally {
                        dispatchingMode = false;
                    }
                }
            } else {
                super.eventDispatched(event, layer);
            }
            layer.repaint();
        }

        public void redispatchMouseEvent(MouseEvent event, JLayer<? extends JPanel> layer) {
            // Get point relative to the whole canvas.
            Point realPoint = event.getPoint();
            realPoint = SwingUtilities.convertPoint(event.getComponent(), realPoint, layer);
            realPoint = Vector.scaled(new Vector(realPoint), getZoomLevel()).toPoint();
            
            Component view = layer.getView();
            Point viewPoint = SwingUtilities.convertPoint(layer, realPoint, view);
            Component target = SwingUtilities.getDeepestComponentAt(layer, realPoint.x, realPoint.y);
            
            // Get point relative to the target.
            Point targetPoint = SwingUtilities.convertPoint(layer, realPoint, target);
            
            if (event.getID() == MouseEvent.MOUSE_CLICKED) {
                System.out.println(getZoomLevel() + " (" + targetPoint.x + ", " + targetPoint.y + ") " + event.getComponent().getClass());
            }

            MouseEvent processedEvent = new MouseEvent(target, event.getID(),
                    event.getWhen(),
                    event.getModifiers(), targetPoint.x, targetPoint.y,
                    event.getClickCount(),
                    event.isPopupTrigger(), event.getButton());
            target.dispatchEvent(processedEvent);
        }
    }
}
