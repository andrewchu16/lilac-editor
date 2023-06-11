package diagram;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.FocusListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class InterfaceDiagram extends Diagram {
    private ArrayList<DiagramBody> bodies;

    public InterfaceDiagram(String titleText, Point pos) {
        super(titleText, pos);
        
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(2, 2, 2, 2);

        this.bodies = new ArrayList<DiagramBody>();
        this.add(new DiagramBody(), constraints);
        this.resizeDiagramToFit();
    }

    public void setMethodText(String methodText) {
        this.getMethodBody().setText(methodText);
    }

    public String getMethodText() {
        return this.getMethodBody().getText();
    }

    public String getLastMethodText() {
        return this.getMethodBody().getLastText();
    }

    public void add(DiagramBody diagramBody, Object constraints) {
        super.add(diagramBody, constraints);
        this.bodies.add(diagramBody);
    }

    public DiagramBody getMethodBody() {
        return this.getBody(0);
    }

    public DiagramBody getBody(int index) {
        return this.bodies.get(index);
    }

    public int getNumBodies() {
        return this.bodies.size();
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        this.getMethodBody().setEnabled(isEnabled);
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() || this.getMethodBody().isEnabled();
    }

    @Override
    public void addFocusListener(FocusListener focusListener) {
        super.addFocusListener(focusListener);
        this.getMethodBody().addFocusListener(focusListener);
    }

    @Override
    public void addMouseListener(MouseListener mouseListener) {
        super.addMouseListener(mouseListener);
        this.getMethodBody().addMouseListener(mouseListener);
    }
    
    @Override
    public boolean isFocusOwner() {
        return super.isFocusOwner() || this.getMethodBody().isFocusOwner();
    }

    @Override
    public void resizeDiagramToFit() {
        DiagramTitle diagramTitle = this.getDiagramTitle();

        int newWidth = Math.max(diagramTitle.getPreferredWidth(), MIN_WIDTH);
        newWidth = Math.max(newWidth, this.getMethodBody().getPreferredWidth());

        int newHeight = diagramTitle.getPreferredHeight() + this.getMethodBody().getPreferredHeight();
        newHeight = Math.max(newHeight, MIN_HEIGHT);

        this.setSize(newWidth + 2, newHeight + 2);
        this.revalidate();
    }
}
