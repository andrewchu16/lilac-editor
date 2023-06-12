package diagram;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import utility.Vector;

abstract public class Diagram extends JPanel {
    public static final int MIN_WIDTH = 150;
    public static final int MIN_HEIGHT = 70;
    private DiagramTitle title;
    private ArrayList<Arrow> arrows;
    
    public Diagram(String titleText, Point pos) {
        super();
        this.setLayout(new GridBagLayout());
        this.setBounds(pos.x, pos.y, MIN_WIDTH, MIN_HEIGHT);
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        this.setBackground(Color.WHITE);
        this.arrows = new ArrayList<Arrow>();

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        
        this.title = new DiagramTitle(titleText);
        this.add(this.title, constraints);
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
        int newWidth = (int) Math.max(this.title.getPreferredWidth(), MIN_WIDTH);
        int newHeight = (int) Math.max(this.title.getPreferredHeight(), MIN_HEIGHT);

        this.setSize(newWidth + 2, newHeight + 2);
        this.revalidate();
    }

    public DiagramTitle getDiagramTitle() {
        return this.title;
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        this.title.setEnabled(isEnabled);
    }

    @Override
    public boolean isEnabled() {
        return this.title.isEnabled();
    }

    @Override
    public void addFocusListener(FocusListener focusListener) {
        this.title.addFocusListener(focusListener);
    }

    @Override
    public void addMouseListener(MouseListener mouseListener) {
        this.title.addMouseListener(mouseListener);
    }

    @Override
    public boolean isFocusOwner() {
        return super.isFocusOwner() || this.title.isFocusOwner();
    }

    public Point[] getArrowMountPoints() {
        Point[] mountPoints = {
            new Point(this.getX() + this.getWidth() / 2, this.getY()), // Top
            new Point(this.getX() + this.getWidth(), this.getY() + this.getHeight() / 2), // Right
            new Point(this.getX() + this.getWidth() / 2, this.getY() + this.getHeight()), // Bottom
            new Point(this.getX(), this.getY() + this.getHeight() / 2) // Left
        };

        return mountPoints;
    }

    public void addArrow(Arrow arrow) {
        this.arrows.add(arrow);
    }

    public void removeArrow(Arrow arrow) {
        this.arrows.remove(arrow);
    }

    public boolean hasArrow(Arrow arrow) {
        return this.arrows.contains(arrow);
    }

    public class DiagramTitle extends JTextField {
        String lastTitleText;

        public DiagramTitle(String titleText) {
            super(titleText);
            this.lastTitleText = titleText;
            this.setHorizontalAlignment(JTextField.CENTER);
            this.addFocusListener(STOP_EDIT_ON_UNFOCUS);
            this.addMouseListener(EDIT_ON_DOUBLECLICK);
            this.getDocument().addDocumentListener(RESIZE_ON_EDIT);
            this.setBackground(Color.WHITE);
            this.setDisabledTextColor(Color.BLACK);
            this.setEnabled(false);
            this.setBorder(BorderFactory.createEmptyBorder());
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

        public int getPreferredWidth() {
            return (int) this.getPreferredSize().getWidth();
        }

        public int getPreferredHeight() {
            return (int) this.getPreferredSize().getHeight();
        }
    }

    public class DiagramBody extends JTextArea {
        String lastText;

        public DiagramBody() {
            super();
            this.lastText = "";
            this.addFocusListener(STOP_EDIT_ON_UNFOCUS);
            this.addMouseListener(EDIT_ON_DOUBLECLICK);
            this.getDocument().addDocumentListener(RESIZE_ON_EDIT);
            this.setDisabledTextColor(Color.BLACK);
            this.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
            this.setEnabled(false);
        }

        public void setText(String text) {
            this.lastText = this.getText();
            super.setText(text);
        }

        public String getLastText() {
            return this.lastText;
        }

        public int getPreferredWidth() {
            return (int) this.getPreferredSize().getWidth();
        }

        public int getPreferredHeight() {
            return (int) this.getPreferredSize().getHeight();
        }
    }

    public final FocusListener STOP_EDIT_ON_UNFOCUS = new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent event) {
            Component component = event.getComponent();
            component.setEnabled(false);
        }
    };

    public final MouseListener EDIT_ON_DOUBLECLICK = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent event) {
            Component component = event.getComponent();
            component.requestFocus();
            if (event.getClickCount() >= 2) {
                component.setEnabled(true);
            }
        }
    };

    public final DocumentListener RESIZE_ON_EDIT = new DocumentListener() {
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
}
