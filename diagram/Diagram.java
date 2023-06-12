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

/**
 * This class represents a Diagram in the UML editor.
 * @author Andrew Chu
 * @version June 2023
 */
abstract public class Diagram extends JPanel {
    public static final int MIN_WIDTH = 150;
    public static final int MIN_HEIGHT = 70;
    private DiagramTitle title;
    private ArrayList<Arrow> arrows;
    
    /**
     * This constructs a new Diagram object.
     * @param titleText The title of this Diagram.
     * @param pos The top-left corner of this Diagram.
     */
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

    /**
     * This gets the position of this Diagram.
     * @return The position.
     */
    public Vector getPos() {
        return new Vector(this.getLocation());
    }

    /**
     * This sets the position of this Diagram.
     * @param pos The new position.
     */
    public void setPos(Vector pos) {
        this.setLocation(pos.toPoint());
        for (Arrow arrow: this.arrows) {
            arrow.calculatePoints();
        }
    }

    /**
     * This shifts the position of this Diagram.
     * @param changeInPos The amount to shift this Diagram.
     */
    public void shiftPos(Vector changeInPos) {
        this.setLocation(this.getX() + (int) changeInPos.getX(), this.getY() + (int) changeInPos.getY());
        for (Arrow arrow: this.arrows) {
            arrow.calculatePoints();
        }
    }

    /**
     * This sets the title.
     * @param titleText The new title.
     */
    public void setTitle(String titleText) {
        this.title.setTitle(titleText);
    }

    /**
     * This gets the title.
     * @return The title.
     */
    public String getTitle() {
        return this.title.getTitle();
    }

    /**
     * This gets the last title of this Diagram if it was changed.
     * @return The previous title.
     */
    public String getLastTitle() {
        return this.title.getLastTitle();
    }

    /**
     * This resizes the Diagram to perfectly fit the text within it.
     */
    public void resizeDiagramToFit() {
        int newWidth = (int) Math.max(this.title.getPreferredWidth(), MIN_WIDTH);
        int newHeight = (int) Math.max(this.title.getPreferredHeight(), MIN_HEIGHT);

        this.setSize(newWidth + 2, newHeight + 2);
        this.revalidate();

        for (Arrow arrow: this.arrows) {
            arrow.calculatePoints();
        }
    }

    /**
     * This gets the DiagramTitle component. 
     * @return The DiagramTitle component.
     */
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

    /**
     * This gets the possible arrow mount points that an arrow can be attached to. They are in the middle of each side of this diagram.
     * @return An array containing the mount points.
     */
    public Point[] getArrowMountPoints() {
        Point[] mountPoints = {
            new Point(this.getX() + this.getWidth() / 2, this.getY()), // Top
            new Point(this.getX() + this.getWidth(), this.getY() + this.getHeight() / 2), // Right
            new Point(this.getX() + this.getWidth() / 2, this.getY() + this.getHeight()), // Bottom
            new Point(this.getX(), this.getY() + this.getHeight() / 2) // Left
        };

        return mountPoints;
    }

    /**
     * This adds an arrow that is attached to this diagram.
     * @param arrow The attached arrow to add.
     */
    public void addArrow(Arrow arrow) {
        this.arrows.add(arrow);
    }

    /**
     * This removes an arrow attached to this diagram.
     * @param arrow The arrow to remove.
     */
    public void removeArrow(Arrow arrow) {
        this.arrows.remove(arrow);
    }

    /**
     * This checks if an arrow is attached to this diagram.
     * @param arrow The arrow to check
     * @return True if an arrow is attached, false if not.
     */
    public boolean hasArrow(Arrow arrow) {
        return this.arrows.contains(arrow);
    }

    /**
     * This checks if any text on this diagram was changed.
     * @return True if it has changed, false if it has not.
     */
    public boolean textChanged() {
        return !this.getTitle().equals(this.getLastTitle());
    }

    /**
     * This class represents the title component of this diagram. It can be edited.
     */
    public class DiagramTitle extends JTextField {
        String lastTitleText;

        /**
         * This constructs a DiagramTitle component.
         * @param titleText The title.
         */
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

        /**
         * This sets the title displayed by this component.
         * @param titleText The new title.
         */
        public void setTitle(String titleText) {
            this.lastTitleText = this.getTitle();
            this.setText(titleText);
        }

        /** 
         * This gets the title stored and displayed by this component.
         */
        public String getTitle() {
            return this.getText();
        }

        /**
         * This gets the last title stored and displayed by this component before this one was set.
         * @return The last title.
         */
        public String getLastTitle() {
            return this.lastTitleText;
        }

        /**
         * This gets the preferred width of this title.
         * @return The width.
         */
        public int getPreferredWidth() {
            return (int) this.getPreferredSize().getWidth();
        }

        /**
         * This gets the preferred height of this title.
         * @return The height.
         */
        public int getPreferredHeight() {
            return (int) this.getPreferredSize().getHeight();
        }
    }

    /**
     * This represents a body section in this diagram. It can store and edit multiple lines of text.
     */
    public class DiagramBody extends JTextArea {
        String lastText;

        /**
         * This constructs a DiagramBody component.
         */
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

        /**
         * This sets the text displayed by this DiagramBody.
         * @param text The text to set.
         */
        public void setText(String text) {
            this.lastText = this.getText();
            super.setText(text);
        }

        /**
         *This gets the last text set before this one.
         * @return The last text set.
         */
        public String getLastText() {
            return this.lastText;
        }

        /**
         * This gets the width of this body.
         * @return The width.
         */
        public int getPreferredWidth() {
            return (int) this.getPreferredSize().getWidth();
        }

        /**
         * This gets the height of this body.
         * @return The height.
         */
        public int getPreferredHeight() {
            return (int) this.getPreferredSize().getHeight();
        }
    }

    /**
     * This represents a focus event listener that disables this component when it loses focus.
     */
    public final FocusListener STOP_EDIT_ON_UNFOCUS = new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent event) {
            Component component = event.getComponent();
            component.setEnabled(false);
        }
    };

    /**
     * This represents a mouse listener that enables this component on double click.
     */
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

    /**
     * This resizes the component to fit the text whenever the text gets edited.
     */
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
