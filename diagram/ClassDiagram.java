package diagram;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Point;

import javax.swing.BorderFactory;

public class ClassDiagram extends Diagram {
    private DiagramBody methods;
    private DiagramBody properties;

    public ClassDiagram(String titleText, Point pos) {
        super(titleText, pos);
        
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = 1;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(2, 2, 2, 2);

        this.properties = new DiagramBody();
        this.properties.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        this.add(this.properties, constraints);

        constraints.gridy = 2;
        this.methods = new DiagramBody();
        this.add(this.methods, constraints);
        this.resizeDiagramToFit();
    }

    public void setMethodText(String methodText) {
        this.methods.setText(methodText);
    }

    public String getMethodText() {
        return this.methods.getText();
    }

    public String getLastMethodText() {
        return this.methods.getLastText();
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        this.methods.setEnabled(isEnabled);
    }

    @Override
    public void resizeDiagramToFit() {
        DiagramTitle diagramTitle = this.getDiagramTitle();

        int newWidth = Math.max(diagramTitle.getPreferredWidth(), MIN_WIDTH);
        newWidth = Math.max(newWidth, this.properties.getPreferredWidth());
        newWidth = Math.max(newWidth, this.methods.getPreferredWidth());

        int newHeight = diagramTitle.getPreferredHeight() + this.properties.getPreferredHeight() + this.methods.getPreferredHeight();
        newHeight = Math.max(newHeight, MIN_HEIGHT);

        this.setSize(newWidth + 2, newHeight + 4);
        this.revalidate();
    }
}
