package editor;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;

import javax.swing.JLabel;
import javax.swing.JPanel;

import math.Vector;

public class Diagram extends JPanel {
    public static final int MIN_WIDTH = 150;
    public static final int MIN_HEIGHT = 100;
    
    public Diagram(String title, Point pos) {
        super();
        this.setLayout(new GridBagLayout());
        this.setBounds(pos.x, pos.y, MIN_WIDTH, MIN_HEIGHT);
        this.setBackground(Color.CYAN);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.add(new JLabel(title), constraints);
        constraints.gridy = 1;
        this.add(new JLabel("Methods"), constraints);
    }

    public Vector getPos() {
        return null;
    }

    public void setPos(Vector pos) {
        
    }

    public void shiftPos(Vector changeInPos) {

    }
}
