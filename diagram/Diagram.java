package diagram;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import math.Vector;

public class Diagram extends JPanel {
    public static final int MIN_WIDTH = 150;
    public static final int MIN_HEIGHT = 70;
    private boolean isLocked;
    private DiagramTitle title;
    
    public Diagram(String titleText, Point pos) {
        super();
        this.setLayout(new GridBagLayout());
        this.setBounds(pos.x, pos.y, MIN_WIDTH, MIN_HEIGHT);
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        this.setBackground(Color.WHITE);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        
        this.title = new DiagramTitle(titleText);
        this.add(this.title, constraints);

        this.setLocked(false);
    }

    public Vector getPos() {
        return new Vector(this.getLocation());
    }

    public void setPos(Vector pos) {
        this.setLocation(pos.toPoint());
    }

    public void shiftPos(Vector changeInPos) {
        this.setLocation(this.getX() + (int) changeInPos.getX(), this.getY() + (int) changeInPos.getY());
    }

    public boolean isLocked() {
        return this.isLocked;
    }

    public void setLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    public void setTitle(String titleText) {
        this.title.setTitle(titleText);
    }

    public String getTitle() {
        return this.title.getTitle();
    }

    public String getLastTitle() {
        return this.title.getLastTitle();
    }

    public void resizeDiagramToFit() {
        int newWidth = Math.max(this.title.getWidth(), MIN_WIDTH);
        int newHeight = Math.max(this.title.getHeight(), MIN_HEIGHT);

        this.setSize(newWidth, newHeight);
        this.revalidate();
    }

    private class DiagramTitle extends JTextField {
        String lastTitleText;

        public DiagramTitle(String titleText) {
            super(titleText);
            this.lastTitleText = titleText;
            this.setHorizontalAlignment(JTextField.CENTER);
            this.addFocusListener(DIAGRAM_TITLE_FOCUS_LISTENER);
            this.addMouseListener(DIAGRAM_TITLE_MOUSE_LISTENER);
            this.getDocument().addDocumentListener(DIAGRAM_TITLE_DOCUMENT_LISTENER);

            this.setBackground(Color.WHITE);
            this.setDisabledTextColor(Color.BLACK);
            this.setEnabled(false);
        }

        public void setTitle(String titleText) {
            this.lastTitleText = this.getTitle();
            this.setText(titleText);
        }

        public String getTitle() {
            return this.getText();
        }

        public String getLastTitle() {
            return this.lastTitleText;
        }

        public final DocumentListener DIAGRAM_TITLE_DOCUMENT_LISTENER = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                resizeDiagramToFit();
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                resizeDiagramToFit();
            }

            @Override
            public void changedUpdate(DocumentEvent event) {}   
        };

        public final FocusListener DIAGRAM_TITLE_FOCUS_LISTENER = new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent event) {
                setEnabled(false);
            }
        };

        public final MouseListener DIAGRAM_TITLE_MOUSE_LISTENER = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() >= 2) {
                    setEnabled(true);
                    requestFocus();
                }
            }
        };
    }
}
