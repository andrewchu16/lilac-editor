package editor;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import math.Vector;

public class Canvas extends JScrollPane {
    private File file;
    private String title;
    private JViewport viewport;
    private InnerCanvasPanel innerPanel;
    private int zoomLevelIndex;
    private EditorActionHistory actionHistory;
    private EditorAction lastSavedAction;
    private ArrayList<Diagram> selectedDiagrams;
    private Dimension originalSize;
    private Tool tool;

    private static final double[] ZOOM_LEVELS = {0.5, 0.75, 0.9, 1.0, 1.15, 1.5, 2.0};
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

        this.getHorizontalScrollBar().setUnitIncrement(4);
        this.getVerticalScrollBar().setUnitIncrement(4);
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        this.zoomLevelIndex = Arrays.binarySearch(ZOOM_LEVELS, 1.0);
        
        this.viewport = new JViewport();
        this.setViewport(viewport);
        
        this.innerPanel = new InnerCanvasPanel();
        this.innerPanel.setPreferredSize(new Dimension(1800, 1200));
        this.originalSize = new Dimension(1800, 1200);
        this.innerPanel.setLayout(null);
        
        JLabel label = new JLabel("EHEELEFJKL");
        label.setBorder(BorderFactory.createLineBorder(Color.PINK, 4));
        label.setBounds(40, 50, 90, 50);

        JLabel label2 = new JLabel("help me");
        label2.setBorder(BorderFactory.createLineBorder(Color.CYAN, 3));
        label2.setBounds(130, 100, 100, 100);

        this.innerPanel.add(label);
        this.innerPanel.add(label2);
        this.viewport.add(innerPanel);

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
            this.repaint();
        }
    }

    private void updateCanvas() {
        double zoomLevel = ZOOM_LEVELS[this.zoomLevelIndex];
        this.innerPanel.setPreferredSize(new Dimension((int) (this.originalSize.getWidth() * zoomLevel), (int) (this.originalSize.getHeight() * zoomLevel)));
        this.viewport.revalidate();
    }

    public void addDiagram(Diagram diagram) {
        this.innerPanel.add(diagram);
        this.innerPanel.repaint();
    }

    public void removeDiagram(Diagram diagram) {
        this.innerPanel.remove(diagram);
        this.innerPanel.repaint();
    }

    public void moveDiagrams(Vector changeInPos) {
        for (Diagram diagram: this.selectedDiagrams) {
            Vector newPos = Vector.sum(diagram.getPos(), changeInPos);
            EditorAction action = new MoveAction(diagram, newPos);
            this.actionHistory.add(action);
        }
    }

    public void editDiagram(Diagram diagram) {

    }

    public void export() {

    }

    public final MouseAdapter CANVAS_MOUSE_LISTENER = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent event) {
            if (tool.getType().equals(Const.SELECT_TOOL_TYPE)) {
                Point pos = event.getPoint();
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
    
    public final MouseMotionAdapter DIAGRAM_MOUSE_LISTENER = new MouseMotionAdapter() {
        @Override
        public void mouseDragged(MouseEvent e) {
        }

        @Override
        public void mouseMoved(MouseEvent e) {
        }
    };

    public class DiagramMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent event) {
            System.out.println("mouseClicked");
            // Edit
        }

        @Override
        public void mousePressed(MouseEvent event) {
        }

        @Override
        public void mouseReleased(MouseEvent event) {
        }

        @Override
        public void mouseEntered(MouseEvent event) {
        }

        @Override
        public void mouseExited(MouseEvent event) {
        }
    }

    private class InnerCanvasPanel extends JPanel {
        @Override
        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.scale(ZOOM_LEVELS[zoomLevelIndex], ZOOM_LEVELS[zoomLevelIndex]);
            super.paint(g2);
        }
    }


    public class CreateDiagramAction implements EditorAction {
        private Diagram diagram;

        public CreateDiagramAction(Diagram diagram) {
            this.diagram = diagram;
        }

        @Override
        public void doAction() {
            addDiagram(this.diagram);
        }

        @Override
        public void undoAction() {
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
        public void doAction() {
            this.diagram.shiftPos(this.changeInPos);
        }

        @Override
        public void undoAction() {
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
        public void doAction() {
            int length = Math.min(diagrams.size(), changeInPosList.size());
            for (int i = 0; i < length; i++) {
                Vector changeInPos = changeInPosList.get(i);
                this.diagrams.get(i).shiftPos(changeInPos);
            }
        }

        @Override
        public void undoAction() {
            int length = Math.min(diagrams.size(), changeInPosList.size());
            for (int i = 0; i < length; i++) {
                Vector changeInPos = Vector.scaled(changeInPosList.get(i), -1);
                this.diagrams.get(i).shiftPos(changeInPos);
            }
        }
    }
}
