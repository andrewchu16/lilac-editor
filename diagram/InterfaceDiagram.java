package diagram;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.FocusListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;

/**
 * This represents a diagram component that stores an interface.
 * @author Andrew Chu
 * @version June 2023
 */
public class InterfaceDiagram extends Diagram {
    private ArrayList<DiagramBody> bodies;

    /**
     * This constructs a new InterfaceDiagram component.
     * @param titleText The title of this diagram.
     * @param pos The position of this diagram.
     */
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

    /**
     * This sets the method text displayed by this diagram.
     * @param methodText The method text.
     */
    public void setMethodText(String methodText) {
        this.getMethodBody().setText(methodText);
    }

    /**
     * This gets the method text displayed and stored by this diagram.
     * @return The method text.
     */
    public String getMethodText() {
        return this.getMethodBody().getText();
    }

    /**
     * This gets the last set method text stored by this diagram before the current one.
     * @return
     */
    public String getLastMethodText() {
        return this.getMethodBody().getLastText();
    }

    /**
     * This adds a diagram body to the bottom of this diagram.
     * @param diagramBody The diagram body component to add
     * @param constraints The constraints on this component.
     */
    public void add(DiagramBody diagramBody, Object constraints) {
        super.add(diagramBody, constraints);
        this.bodies.add(diagramBody);
    }

    /**
     * This gets the method body diagram component.
     * @return The method body diagram component.
     */
    public DiagramBody getMethodBody() {
        return this.getBody(0);
    }

    /**
     * This gets the nth diagram body component in this diagram.
     * @param index The index of the diagram body to get.
     * @return The diagram body component.
     */
    public DiagramBody getBody(int index) {
        return this.bodies.get(index);
    }

    /**
     * This gets the number of diagram body components in this diagram.
     * @return The number of diagram bodies.
     */
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
    public boolean textChanged() {
        return super.textChanged() || !this.getMethodText().equals(this.getLastMethodText());
    }

    @Override
    public void resizeDiagramToFit() {
        if (this.getNumBodies() != 1) {
            return;
        }
        
        DiagramTitle diagramTitle = this.getDiagramTitle();

        int newWidth = Math.max(diagramTitle.getPreferredWidth(), MIN_WIDTH);
        newWidth = Math.max(newWidth, this.getMethodBody().getPreferredWidth());

        int newHeight = diagramTitle.getPreferredHeight() + this.getMethodBody().getPreferredHeight();
        newHeight = Math.max(newHeight, MIN_HEIGHT);

        this.setSize(newWidth + 2, newHeight + 2);
        this.revalidate();
    }
}
