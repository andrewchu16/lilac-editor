import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

public class Canvas extends JScrollPane {
    private File file;
    private String title;
    private JViewport viewport;
    private InnerCanvasPanel innerPanel;
    private JLabel label;
    private int zoomLevelIndex;

    private static final double[] ZOOM_LEVELS = {0.25, 0.5, 0.75, 0.9, 1.0, 1.15, 1.5, 2.0, 3.0};

    public Canvas() {
        super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.file = new File("./untitled.canvas");
        this.title = this.getFileName();
        this.getHorizontalScrollBar().setUnitIncrement(4);
        this.getVerticalScrollBar().setUnitIncrement(4);
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        this.zoomLevelIndex = Arrays.binarySearch(ZOOM_LEVELS, 1.0);
        
        this.viewport = new JViewport();
        this.setViewport(viewport);
        
        this.innerPanel = new InnerCanvasPanel();
        this.innerPanel.setPreferredSize(new Dimension(1800, 1200));
        this.innerPanel.setLayout(null);
        
        label = new JLabel("EHEELEFJKL");
        label.setBorder(BorderFactory.createLineBorder(Color.PINK, 4));
        label.setBounds(40, 50, 90, 50);

        JLabel label2 = new JLabel("help me");
        label2.setBorder(BorderFactory.createLineBorder(Color.CYAN, 3));
        label2.setBounds(130, 100, 100, 100);

        this.innerPanel.add(label);
        this.innerPanel.add(label2);
        this.viewport.add(innerPanel);
    }

    public Canvas(String filePath) {
        this.file = new File(filePath);
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
        return true;
    }

    public boolean canRedo() {
        return true;
    }

    public void save() {
        System.out.println(this.getSize());
    }

    public boolean isSaved() {
        return true;
    }

    public boolean undo() {
        return true;
    }

    public boolean redo() {
        return true;
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
            this.innerPanel.repaint();
        }
    }

    public void zoomOut() {
        if (this.canZoomOut()) {
            this.zoomLevelIndex--;
            this.innerPanel.repaint();
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
}
