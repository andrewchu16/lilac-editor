package editor;
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
    private boolean isEditable;
    private JTextField titleLabel;
    
    public Diagram(String title, Point pos) {
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
        constraints.fill = GridBagConstraints.CENTER;
        
        this.titleLabel = new JTextField(title);
        this.titleLabel.setHorizontalAlignment(JTextField.CENTER);
        this.titleLabel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        this.titleLabel.getDocument().addDocumentListener(PACK_TEXT_ON_EDIT);
        this.titleLabel.addFocusListener(STOP_EDITING_ON_UNFOCUS);
        this.titleLabel.addMouseListener(EDIT_ON_CLICK);
        this.add(titleLabel, constraints);

        this.setLocked(false);
        this.setEditable(false);
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

    public boolean isEditable() {
        return this.isEditable;
    }

    public void setEditable(boolean isEditable) {
        this.isEditable = isEditable;
        this.titleLabel.setEditable(isEditable);
        
        if (isEditable) {
            this.titleLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        } else {
            this.titleLabel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        }
    }

    public void packText() {
        Dimension titleSize = this.titleLabel.getPreferredSize();
        titleSize.width = Math.max(MIN_WIDTH, titleSize.width + 2);
        
        this.titleLabel.setSize(titleSize);
        this.setSize(titleSize.width + 2, this.getHeight());
        this.revalidate();
    }

    public final DocumentListener PACK_TEXT_ON_EDIT = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent event) {
            packText();
        }

        @Override
        public void removeUpdate(DocumentEvent event) {
            packText();
        }

        @Override
        public void changedUpdate(DocumentEvent event) {}
    };

    public final FocusListener STOP_EDITING_ON_UNFOCUS = new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent event) {
            setEditable(false);
        }
        
    };

    public final MouseAdapter EDIT_ON_CLICK = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent event) {
            if (event.getClickCount() >= 2 && event.getButton() == MouseEvent.BUTTON1) {
                setEditable(true);
            }
        }
    };
}
