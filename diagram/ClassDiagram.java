package diagram;

import java.awt.GridBagConstraints;
import java.awt.Point;

public class ClassDiagram extends Diagram {
    public ClassDiagram(String title, Point pos) {
        super(title, pos);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = 1;
    }
}
